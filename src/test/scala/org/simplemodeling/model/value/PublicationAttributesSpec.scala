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

/*
 * @since   Dec. 22, 2025
 *  version Mar. 29, 2026
 * @version Sep. 28, 2026
 * @author  ASAMI, Tomoharu
 */
final class PublicationAttributesSpec extends AnyWordSpec
    with GivenWhenThen with Matchers with ScalaCheckDrivenPropertyChecks {
  "PublicationAttributes storage interpretation" should {
    "read all publication timestamps with their original UTC instants" in {
      forAll(Gen.chooseNum(0L, 4102444800L)) { seconds =>
        Given("a storage record containing every publication timestamp")
        val timestamp = Instant.ofEpochSecond(seconds).atZone(ZoneOffset.UTC)
        val record = Record.dataAuto(
          "publish_at" -> timestamp.toString, "public_at" -> timestamp.toString,
          "close_at" -> timestamp.toString, "start_at" -> timestamp.toString,
          "end_at" -> timestamp.toString
        )
        When("the publication storage value is interpreted")
        val result = summon[ValueReader[PublicationAttributes]].readC(record).toOption.get
        Then("no timestamp is omitted or reinterpreted")
        Vector(result.publishAt, result.publicAt, result.closeAt, result.startAt, result.endAt)
          .map(_.map(_.toInstant)) shouldBe Vector.fill(5)(Some(timestamp.toInstant))
      }
    }

    "reject malformed nonempty timestamps at every publication field" in {
      forAll(Gen.oneOf("publish_at", "public_at", "close_at", "start_at", "end_at"), Gen.alphaStr) { (key, suffix) =>
        Given("a nonempty malformed timestamp in a publication record")
        val record = Record.dataAuto(key -> ("invalid:" + suffix))
        When("the publication reader interprets the timestamp")
        val result = summon[ValueReader[PublicationAttributes]].readC(record)
        Then("the invalid timestamp produces a failure instead of an absent field")
        result shouldBe a[Consequence.Failure[_]]
      }
    }
  }
}
