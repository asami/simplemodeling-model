package org.simplemodeling.model.value

import org.scalacheck.Gen
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import org.simplemodeling.model.ModelHygieneFixtures

/*
 * @since   Dec. 22, 2025
 *  version Mar. 29, 2026
 * @version Sep. 28, 2026
 * @author  ASAMI, Tomoharu
 */
final class SimpleObjectContentSpec extends AnyWordSpec
    with GivenWhenThen with Matchers with ScalaCheckDrivenPropertyChecks {
  "SimpleObjectContent composition" should {
    "delegate every attribute group through the same composed object" in {
      forAll(Gen.alphaStr) { text =>
        Given("a complete composed value with a variable content body")
        val supplied = ModelHygieneFixtures.content("owner", "body" + text)
        When("the value is read through its standard holder")
        val result = ModelHygieneFixtures.objectView(supplied)
        Then("all ten attribute groups retain their original instances")
        result.nameAttributes.eq(supplied.nameAttributes) shouldBe true
        result.descriptiveAttributes.eq(supplied.descriptiveAttributes) shouldBe true
        result.contentAttributes.eq(supplied.contentAttributes) shouldBe true
        result.lifecycleAttributes.eq(supplied.lifecycleAttributes) shouldBe true
        result.publicationAttributes.eq(supplied.publicationAttributes) shouldBe true
        result.securityAttributes.eq(supplied.securityAttributes) shouldBe true
        result.resourceAttributes.eq(supplied.resourceAttributes) shouldBe true
        result.auditAttributes.eq(supplied.auditAttributes) shouldBe true
        result.mediaAttributes.eq(supplied.mediaAttributes) shouldBe true
        result.contextualAttribute.eq(supplied.contextualAttribute) shouldBe true
      }
    }

    "keep unrelated metadata intact when content is replaced" in {
      Given("an original composed value and a replacement body")
      val supplied = ModelHygieneFixtures.content("owner", "original")
      val replacement = ContentAttributes(content = Some(ContentBody("replacement")))
      When("a new composed value replaces only content")
      val result = supplied.copy(contentAttributes = replacement)
      Then("the new body is visible and ownership and lifecycle metadata are retained")
      ModelHygieneFixtures.objectView(result).content.map(_.value) shouldBe Some("replacement")
      result.securityAttributes.eq(supplied.securityAttributes) shouldBe true
      result.lifecycleAttributes.eq(supplied.lifecycleAttributes) shouldBe true
      supplied.contentAttributes.content.map(_.value) shouldBe Some("original")
    }
  }
}
