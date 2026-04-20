package org.simplemodeling.model.value

import org.goldenport.datatype.Identifier
import org.goldenport.datatype.I18nLabel
import org.goldenport.datatype.I18nTitle
import org.goldenport.datatype.Name
import org.goldenport.datatype.Slug
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

/*
 * @since   Dec. 22, 2025
 * @version Apr. 20, 2026
 * @author  ASAMI, Tomoharu
 */
class NameAttributesSpec extends AnyWordSpec
  with ScalaCheckDrivenPropertyChecks
  with Matchers {

  "NameAttributes" should {
    "project summary for UI list use" in {
      val src = org.goldenport.value.NameAttributes(
        name = Name("Alice"),
        label = Some(I18nLabel("Alice Label")),
        title = Some(I18nTitle("Reader")),
        code = Some(Identifier("alicecode")),
        alias = None,
        slug = Some(Slug.create("alice").TAKE)
      )

      val projected = org.simplemodeling.model.value.summary.NameAttributes(src)

      projected.name.value shouldBe "Alice"
      projected.title.map(_.value.displayMessage) shouldBe Some("Reader")
      projected.alias shouldBe None
    }

    "project detail for UI detail use" in {
      val src = org.goldenport.value.NameAttributes(
        name = Name("Alice"),
        label = Some(I18nLabel("Alice Label")),
        title = Some(I18nTitle("Reader")),
        code = Some(Identifier("alicecode")),
        alias = None,
        slug = Some(Slug.create("alice").TAKE)
      )

      val projected = org.simplemodeling.model.value.detail.NameAttributes(src)

      projected.code.map(_.value) shouldBe Some("alicecode")
      projected.slug.map(_.value) shouldBe Some("alice")
    }
  }
}
