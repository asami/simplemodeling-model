package org.simplemodeling.model.value

import org.scalacheck.Gen
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import org.goldenport.Consequence
import org.simplemodeling.model.value.internal.ProjectionValueReaders

/*
 * @since   Dec. 22, 2025
 *  version Mar. 29, 2026
 * @version Sep. 28, 2026
 * @author  ASAMI, Tomoharu
 */
final class AuditAttributesSpec extends AnyWordSpec
    with GivenWhenThen with Matchers with ScalaCheckDrivenPropertyChecks {
  "AuditAttributes projection interpretation" should {
    "retain the supplied marker instance without inventing projection data" in {
      Given("an explicitly supplied typed marker")
      val supplied = AuditAttributes()
      When("the marker is read at the projection boundary")
      val result = ProjectionValueReaders.auditAttributes.readC(supplied)
      Then("the same supplied value is returned")
      result.toOption.map(_.eq(supplied)) shouldBe Some(true)
    }

    "reject untyped strings instead of treating them as an empty marker" in {
      forAll(Gen.alphaStr) { text =>
        Given("an untyped projection string, including the empty string")
        val supplied: Any = text
        When("the projection reader interprets the value")
        val result = ProjectionValueReaders.auditAttributes.readC(supplied)
        Then("the incompatible value is rejected rather than defaulted")
        result shouldBe a[Consequence.Failure[_]]
      }
    }
  }
}
