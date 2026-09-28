package org.simplemodeling.model

import org.scalacheck.Gen
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import org.simplemodeling.model.value.SecurityAttributes

/*
 * @since   Dec. 22, 2025
 *  version Mar. 29, 2026
 * @version Sep. 28, 2026
 * @author  ASAMI, Tomoharu
 */
final class SimpleObjectSpec extends AnyWordSpec
    with GivenWhenThen with Matchers with ScalaCheckDrivenPropertyChecks {
  "SimpleObject attribute views" should {
    "delegate body and ownership without changing the supplied metadata" in {
      forAll(Gen.alphaStr, Gen.alphaStr) { (suffix, text) =>
        Given("a complete object with an arbitrary owner and content body")
        val supplied = ModelHygieneFixtures.content("actor" + suffix, "body" + text)
        When("the composed model is viewed as a SimpleObject")
        val result = ModelHygieneFixtures.objectView(supplied)
        Then("content and each security identifier are delegated to the supplied attributes")
        result.content.map(_.value) shouldBe Some("body" + text)
        result.ownerId shouldBe supplied.securityAttributes.ownerId.id.value
        result.groupId shouldBe supplied.securityAttributes.groupId.id.value
        result.privilegeId shouldBe supplied.securityAttributes.privilegeId.id.value
      }
    }

    "keep owner, group and privilege identities independent" in {
      Given("an object with distinct security principals")
      val supplied = ModelHygieneFixtures.content("owner", "body")
      val owner = SecurityAttributes.ownedBy("owner")
      val group = SecurityAttributes.ownedBy("group")
      val privilege = SecurityAttributes.ownedBy("privilege")
      val content = supplied.copy(securityAttributes = owner.copy(
        groupId = group.ownerId, privilegeId = privilege.ownerId
      ))
      When("the object's security identity projections are read")
      val result = ModelHygieneFixtures.objectView(content)
      Then("each projection uses its own principal rather than the owner fallback")
      result.ownerId shouldBe owner.ownerId.id.value
      result.groupId shouldBe group.ownerId.id.value
      result.privilegeId shouldBe privilege.ownerId.id.value
    }
  }
}
