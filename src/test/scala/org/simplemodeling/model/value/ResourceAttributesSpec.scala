package org.simplemodeling.model.value

import org.scalacheck.Gen
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import java.time.{Instant, ZoneOffset}
import org.goldenport.Consequence
import org.goldenport.convert.ValueReader
import org.goldenport.record.Record
import org.simplemodeling.model.statemachine.ActivationStatus

/*
 * @since   Dec. 22, 2025
 *  version Mar. 29, 2026
 * @version Sep. 28, 2026
 * @author  ASAMI, Tomoharu
 */
final class ResourceAttributesSpec extends AnyWordSpec
    with GivenWhenThen with Matchers with ScalaCheckDrivenPropertyChecks {
  "ResourceAttributes storage interpretation" should {
    "retain stored activation state independently of its timestamps" in {
      forAll(Gen.oneOf(ActivationStatus.values.toSeq), Gen.chooseNum(0L, 4102444800L)) { (state, seconds) =>
        Given("a stored resource with activation, deactivation and expiry timestamps")
        val timestamp = Instant.ofEpochSecond(seconds).atZone(ZoneOffset.UTC)
        val record = Record.dataAuto(
          "activation_status" -> state.dbValue, "activated_at" -> timestamp.toString,
          "deactivated_at" -> timestamp.toString, "expires_at" -> timestamp.toString
        )
        When("the resource storage value is interpreted")
        val result = summon[ValueReader[ResourceAttributes]].readC(record).toOption.get
        Then("the stored status is retained without inferring a different lifecycle state")
        result.activationStatus shouldBe state
        Vector(result.activatedAt, result.deactivatedAt, result.expiresAt)
          .map(_.map(_.toInstant)) shouldBe Vector.fill(3)(Some(timestamp.toInstant))
      }
    }

    "reject malformed nonempty timestamps rather than silently clearing them" in {
      forAll(Gen.oneOf("activated_at", "deactivated_at", "expires_at"), Gen.alphaStr) { (key, suffix) =>
        Given("a resource record with a malformed timestamp")
        val record = Record.dataAuto(key -> ("invalid:" + suffix))
        When("the resource reader interprets the record")
        val result = summon[ValueReader[ResourceAttributes]].readC(record)
        Then("a failure is returned at the invalid field")
        result shouldBe a[Consequence.Failure[_]]
      }
    }
  }
}
