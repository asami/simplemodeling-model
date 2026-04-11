package org.simplemodeling.model.value

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/*
 * @since   Mar. 25, 2026
 * @version Apr.  3, 2026
 * @author  ASAMI, Tomoharu
 */
class GeneratedAddressReferenceSpec extends AnyFlatSpec with Matchers {
  private def generatedAddressAvailable: Boolean =
    scala.util.Try(Class.forName("org.simplemodeling.model.value.Address$")).isSuccess &&
      scala.util.Try(Class.forName("org.simplemodeling.model.value.CountryCode$")).isSuccess

  private def _new_typed_value(name: String, value: String): AnyRef = {
    val module = Class.forName(s"org.simplemodeling.model.value.${name}$$").getField("MODULE$").get(null)
    module.getClass.getMethod("apply", classOf[String]).invoke(module, value).asInstanceOf[AnyRef]
  }

  "generated address value objects" should "be referenceable from handwritten code" in {
    if (!generatedAddressAvailable) cancel("generated address model is not available")

    val countryModule = Class.forName("org.simplemodeling.model.value.CountryCode$").getField("MODULE$").get(null)
    val country = countryModule.getClass.getMethod("apply", classOf[String]).invoke(countryModule, "JP")
    val countryValue = country.getClass.getMethod("value").invoke(country)

    val addressModule = Class.forName("org.simplemodeling.model.value.Address$").getField("MODULE$").get(null)
    val address = addressModule.getClass.getMethod(
      "apply",
      Class.forName("org.simplemodeling.model.value.CountryCode"),
      Class.forName("org.simplemodeling.model.value.PostalCode"),
      Class.forName("org.simplemodeling.model.value.Region"),
      Class.forName("org.simplemodeling.model.value.Locality"),
      Class.forName("org.simplemodeling.model.value.SubLocality"),
      Class.forName("org.simplemodeling.model.value.StreetAddress"),
      Class.forName("org.simplemodeling.model.value.ExtendedAddress")
    ).invoke(
      addressModule,
      _new_typed_value("CountryCode", "JP"),
      _new_typed_value("PostalCode", "160-0022"),
      _new_typed_value("Region", "Tokyo"),
      _new_typed_value("Locality", "Shinjuku-ku"),
      _new_typed_value("SubLocality", "Shinjuku"),
      _new_typed_value("StreetAddress", "1-2-3"),
      _new_typed_value("ExtendedAddress", "Building A 101")
    )

    val addressCountry = address.getClass.getMethod("addressCountry").invoke(address)
    val postalCode = address.getClass.getMethod("postalCode").invoke(address)
    val addressCountryValue = addressCountry.getClass.getMethod("value").invoke(addressCountry)
    val postalCodeValue = postalCode.getClass.getMethod("value").invoke(postalCode)

    countryValue shouldBe "JP"
    addressCountryValue shouldBe "JP"
    postalCodeValue shouldBe "160-0022"
  }
}
