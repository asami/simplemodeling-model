package org.simplemodeling.model.value

import org.scalatest.GivenWhenThen
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

final class NominalScalarSpec extends AnyWordSpec with Matchers with GivenWhenThen {
  "NominalScalar" should {
    "expose the underlying external value without defining structured record semantics" in {
      Given("a domain-specific scalar")
      final case class LoginName(value: String) extends NominalScalar

      When("the generic nominal-scalar contract is inspected")
      val scalar: NominalScalar = LoginName("alice")

      Then("its external value remains the underlying scalar")
      scalar.value shouldBe "alice"
    }
  }
}
