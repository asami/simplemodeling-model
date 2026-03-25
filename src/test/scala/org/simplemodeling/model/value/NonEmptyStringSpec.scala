package org.simplemodeling.model.value

import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class NonEmptyStringSpec extends AnyFlatSpec with Matchers with EitherValues {
  "NonEmptyString.from" should "create instance from non-empty value" in {
    val result = NonEmptyString.from("abc")

    result.value.value shouldBe "abc"
  }

  it should "reject empty value" in {
    val result = NonEmptyString.from("")

    result.left.value should include("non-empty")
  }
}
