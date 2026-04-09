package org.simplemodeling.model.value

import java.net.URI
import java.time.LocalDate
import java.util.{Locale, TimeZone}

import org.goldenport.datatype.Name
import org.scalatest.funspec.AnyFunSpec

/*
 * @since   Apr.  9, 2026
 * @version Apr.  9, 2026
 * @author  ASAMI, Tomoharu
 */
class ProfileDelegationHoldersSpec extends AnyFunSpec {
  describe("Profile delegation holders") {
    it("exposes identity presentation fields from parent holder") {
      val p = IdentityPresentation(
        familyName = Name("Yamada"),
        givenName = Name("Hanako"),
        familyNameKana = Name("ヤマダ"),
        givenNameKana = Name("ハナコ"),
        displayName = Name("Hanako Yamada"),
        nickname = Name("hana"),
        avatarUrl = URI.create("https://example.com/avatar.png").toURL
      )
      val holder = new IdentityPresentationHolder {
        override val identityPresentation: Option[IdentityPresentation] = Some(p)
      }
      assert(holder.displayName.map(_.toString).contains("Hanako Yamada"))
      assert(holder.familyName.map(_.toString).contains("Yamada"))
    }

    it("exposes personal profile fields from parent holder") {
      val profile = PersonalProfile(
        birthday = LocalDate.parse("2000-01-01"),
        locale = Locale.forLanguageTag("ja-JP"),
        timeZone = TimeZone.getTimeZone("Asia/Tokyo"),
        address = Address(
          addressCountry = CountryCode("JP"),
          postalCode = PostalCode("100-0001"),
          addressRegion = Region("Tokyo"),
          addressLocality = Locality("Chiyoda"),
          addressSubLocality = SubLocality("Marunouchi"),
          streetAddress = StreetAddress("1-1-1"),
          extendedAddress = ExtendedAddress("Building")
        )
      )
      val holder = new PersonalProfileHolder {
        override val personalProfile: Option[PersonalProfile] = Some(profile)
      }
      assert(holder.birthday.contains(LocalDate.parse("2000-01-01")))
      assert(holder.address.nonEmpty)
    }

    it("exposes organization support fields from parent holder") {
      val org = OrganizationSupport(Name("ACME"), Name("R&D"), Name("Engineer"))
      val holder = new OrganizationSupportHolder {
        override val organizationSupport: Option[OrganizationSupport] = Some(org)
      }
      assert(holder.organization.map(_.toString).contains("ACME"))
      assert(holder.jobTitle.map(_.toString).contains("Engineer"))
    }
  }
}
