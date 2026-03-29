package org.simplemodeling.model.value

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/*
 * @since   Mar. 25, 2026
 * @version Mar. 29, 2026
 * @author  ASAMI, Tomoharu
 */
class GeneratedAddressReferenceSpec extends AnyFlatSpec with Matchers {
  private def generatedAddressAvailable: Boolean =
    scala.util.Try(Class.forName("domain.value.Address$")).isSuccess &&
      scala.util.Try(Class.forName("domain.value.CountryCode$")).isSuccess

  "generated address value objects" should "be referenceable from handwritten code" in {
    if (!generatedAddressAvailable) cancel("generated address model is not available")

    val countryModule = Class.forName("domain.value.CountryCode$").getField("MODULE$").get(null)
    val country = countryModule.getClass.getMethod("apply", classOf[String]).invoke(countryModule, "JP")
    val countryValue = country.getClass.getMethod("value").invoke(country)

    val addressModule = Class.forName("domain.value.Address$").getField("MODULE$").get(null)
    val address = addressModule.getClass.getMethods.find { m =>
      m.getName == "apply" && m.getParameterCount == 7
    }.get.invoke(
      addressModule,
      "JP",
      "160-0022",
      "Tokyo",
      "Shinjuku-ku",
      "Shinjuku",
      "1-2-3",
      "Building A 101"
    )

    val addressCountry = address.getClass.getMethod("addressCountry").invoke(address)
    val postalCode = address.getClass.getMethod("postalCode").invoke(address)

    countryValue shouldBe "JP"
    addressCountry shouldBe "JP"
    postalCode shouldBe "160-0022"
  }
}
