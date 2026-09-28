package org.simplemodeling.model.statemachine

import org.scalacheck.Gen
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

/*
 * @since   Dec. 22, 2025
 *  version Mar. 29, 2026
 * @version Sep. 28, 2026
 * @author  ASAMI, Tomoharu
 */
final class StateMachineSpec extends AnyWordSpec
    with GivenWhenThen with Matchers with ScalaCheckDrivenPropertyChecks {
  "StateMachine identity" should {
    "expose canonical names and persistence values across the built-in state families" in {
      Given("the documented model states and their stored identifiers")
      val states = Vector[(StateMachine, String, Int)](
        (PostStatus.Draft, "draft", 1), (PostStatus.Published, "published", 2),
        (PostStatus.Archived, "archived", 3),
        (Aliveness.Alive, "alive", 1), (Aliveness.Suspended, "suspended", 2),
        (Aliveness.Dead, "dead", 3),
        (ActivationStatus.Inactive, "inactive", 1), (ActivationStatus.Active, "active", 2),
        (ActivationStatus.Deactivated, "deactivated", 3), (ActivationStatus.Expired, "expired", 4)
      )
      forAll(Gen.oneOf(states)) { case (state, name, number) =>
        When("a state is viewed through the shared StateMachine contract")
        val actual = (state.stateName, state.dbValue)
        Then("its name and persistence identifier retain their canonical meaning")
        actual shouldBe (name, number)
      }
    }

    "keep database identifiers unambiguous within each family" in {
      Given("the three built-in state families")
      val families = Vector(
        PostStatus.values.toVector, Aliveness.values.toVector, ActivationStatus.values.toVector
      )
      When("each family's persistence and name identities are collected")
      val identities = families.map(states => (states.map(_.dbValue), states.map(_.stateName)))
      Then("no two states in a family share an identifier or name")
      identities.foreach { case (numbers, names) =>
        numbers.distinct.size shouldBe numbers.size
        names.distinct.size shouldBe names.size
      }
    }
  }
}
