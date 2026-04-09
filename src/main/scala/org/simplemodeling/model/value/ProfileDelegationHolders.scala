package org.simplemodeling.model.value

import java.net.URL
import java.time.LocalDate
import java.util.{Locale, TimeZone}

import org.goldenport.datatype.Name

/*
 * @since   Apr.  9, 2026
 * @version Apr.  9, 2026
 * @author  ASAMI, Tomoharu
 */
trait IdentityPresentationHolder {
  def identityPresentation: Option[IdentityPresentation]

  def familyName: Option[Name] = identityPresentation.map(_.familyName)
  def givenName: Option[Name] = identityPresentation.map(_.givenName)
  def familyNameKana: Option[Name] = identityPresentation.map(_.familyNameKana)
  def givenNameKana: Option[Name] = identityPresentation.map(_.givenNameKana)
  def displayName: Option[Name] = identityPresentation.map(_.displayName)
  def nickname: Option[Name] = identityPresentation.map(_.nickname)
  def avatarUrl: Option[URL] = identityPresentation.map(_.avatarUrl)
}

trait PersonalProfileHolder {
  def personalProfile: Option[PersonalProfile]

  def birthday: Option[LocalDate] = personalProfile.map(_.birthday)
  def locale: Option[Locale] = personalProfile.map(_.locale)
  def timeZone: Option[TimeZone] = personalProfile.map(_.timeZone)
  def address: Option[Address] = personalProfile.map(_.address)
}

trait OrganizationSupportHolder {
  def organizationSupport: Option[OrganizationSupport]

  def organization: Option[Name] = organizationSupport.map(_.organization)
  def department: Option[Name] = organizationSupport.map(_.department)
  def jobTitle: Option[Name] = organizationSupport.map(_.jobTitle)
}
