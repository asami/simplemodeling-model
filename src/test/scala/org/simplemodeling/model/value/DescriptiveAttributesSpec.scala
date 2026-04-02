package org.simplemodeling.model.value

import org.goldenport.datatype.I18nBrief
import org.goldenport.datatype.I18nDescription
import org.goldenport.datatype.I18nSummary
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

/*
 * @since   Dec. 22, 2025
 * @version Apr.  2, 2026
 *  version Apr.  2, 2026
 * @author  ASAMI, Tomoharu
 */
class DescriptiveAttributesSpec extends AnyWordSpec
  with ScalaCheckDrivenPropertyChecks
  with Matchers {

  "DescriptiveAttributes" should {
    "project summary for UI list use" in {
      val src = org.goldenport.value.DescriptiveAttributes(
        headline = Some(I18nBrief("Headline")),
        brief = Some(I18nBrief("Brief")),
        summary = Some(I18nSummary("Summary")),
        description = Some(I18nDescription("Long description"))
      )

      val projected = org.simplemodeling.model.value.summary.DescriptiveAttributes(src)

      projected.summary.map(_.toI18nString.displayMessage) shouldBe Some("Summary")
      projected.description shouldBe None
    }

    "project detail for UI detail use" in {
      val src = org.goldenport.value.DescriptiveAttributes(
        headline = Some(I18nBrief("Headline")),
        brief = Some(I18nBrief("Brief")),
        summary = Some(I18nSummary("Summary")),
        description = Some(I18nDescription("Long description"))
      )

      val projected = org.simplemodeling.model.value.detail.DescriptiveAttributes(src)

      projected.description.map(_.toI18nString.displayMessage) shouldBe Some("Long description")
      projected.summary.map(_.toI18nString.displayMessage) shouldBe Some("Summary")
    }
  }
}
