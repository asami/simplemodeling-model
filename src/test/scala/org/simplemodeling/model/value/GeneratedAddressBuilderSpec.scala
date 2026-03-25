package org.simplemodeling.model.value

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/*
 * @since   Mar. 25, 2026
 * @version Mar. 25, 2026
 * @author  ASAMI, Tomoharu
 */
class GeneratedAddressBuilderSpec extends AnyFlatSpec with Matchers {
  "generated CountryCode builder" should "validate the pattern at value creation time" in {
    val result = intercept[IllegalArgumentException] {
      domain.value.CountryCode("jp")
    }

    result.getMessage should include ("value must match")
  }

  "generated Address builder" should "validate required fields at build time" in {
    val builder = domain.value.Address.Builder()
      .withAddressCountry("JP")
      .withPostalCode("160-0022")

    val result = intercept[org.goldenport.ConsequenceException] {
      builder.build()
    }

    result.getMessage.nonEmpty shouldBe true
  }
}
