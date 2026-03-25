package org.simplemodeling.model.value

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/*
 * @since   Mar. 25, 2026
 * @version Mar. 25, 2026
 * @author  ASAMI, Tomoharu
 */
class GeneratedAddressReferenceSpec extends AnyFlatSpec with Matchers {
  "generated address value objects" should "be referenceable from handwritten code" in {
    val country = domain.value.CountryCode("JP")
    val address = domain.value.Address(
      addressCountry = "JP",
      postalCode = "160-0022",
      addressRegion = "Tokyo",
      addressLocality = "Shinjuku-ku",
      addressSubLocality = "Shinjuku",
      streetAddress = "1-2-3",
      extendedAddress = "Building A 101"
    )

    country.value shouldBe "JP"
    address.addressCountry shouldBe "JP"
    address.postalCode shouldBe "160-0022"
  }
}
