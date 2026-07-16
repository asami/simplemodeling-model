package org.simplemodeling.model.value

import org.goldenport.Consequence
import org.goldenport.convert.ValueReader
import org.goldenport.datatype.I18nText
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

/*
 * @since   Jul. 16, 2026
 * @version Jul. 16, 2026
 * @author  ASAMI, Tomoharu
 */
final class ContentBodyBoundarySpec extends AnyWordSpec with Matchers with GivenWhenThen {
  "ContentBody" should {
    "remain a single document instead of collapsing locale-aware text" in {
      Given("a locale-aware narrative with its own storage semantics")
      val localized = I18nText("localized narrative")

      When("the SimpleModeling content boundary decodes that value")
      val result = summon[ValueReader[ContentBody]].readC(localized)

      Then("the implicit display projection is rejected")
      result shouldBe a[Consequence.Failure[_]]

      And("explicit single-document input remains accepted")
      summon[ValueReader[ContentBody]].readC("document body") shouldBe
        Consequence.success(ContentBody("document body"))
    }
  }
}
