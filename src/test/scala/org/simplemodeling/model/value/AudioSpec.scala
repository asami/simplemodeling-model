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
final class AudioSpec extends AnyWordSpec
    with GivenWhenThen with Matchers with ScalaCheckDrivenPropertyChecks {
  "Audio metadata delegation" should {
    "expose embedded content and security through its SimpleObject view" in {
      forAll(Gen.alphaStr, Gen.alphaStr) { (suffix, text) =>
        Given("a media identity and composed object metadata")
        val identity = SecurityAttributes.ownedBy("asset" + suffix).ownerId
        val supplied = ModelHygieneFixtures.content("owner" + suffix, "body" + text)
        When("the media value is interpreted through its model attributes")
        val result = Audio(identity, supplied)
        Then("its media identity and delegated object metadata remain distinct")
        result.id shouldBe identity
        result.content.map(_.value) shouldBe Some("body" + text)
        result.ownerId shouldBe supplied.securityAttributes.ownerId.id.value
        result.lifecycleAttributes.eq(supplied.lifecycleAttributes) shouldBe true
      }
    }

    "retain ownership and body when a distinct media identity is assigned" in {
      Given("a media value whose identity differs from its owner")
      val supplied = ModelHygieneFixtures.content("owner", "original body")
      val original = Audio(SecurityAttributes.ownedBy("firstasset").ownerId, supplied)
      val replacement = SecurityAttributes.ownedBy("secondasset").ownerId
      When("a copy receives the replacement media identity")
      val result = original.copy(id = replacement)
      Then("only the media identity changes and the composed metadata stays intact")
      result.id shouldBe replacement
      result.id should not be original.id
      result.simpleobject.eq(supplied) shouldBe true
      result.ownerId shouldBe original.ownerId
      result.content shouldBe original.content
    }
  }
}
