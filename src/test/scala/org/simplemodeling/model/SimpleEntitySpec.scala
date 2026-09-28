package org.simplemodeling.model

import org.scalacheck.Gen
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import java.time.Instant
import java.util.Locale
import org.simplemodeling.model.datatype.{EntityCollectionId, EntityId, EntityRevision}
import org.simplemodeling.model.value.ContentAttributes

/*
 * @since   Dec. 22, 2025
 *  version Mar. 29, 2026
 * @version Sep. 28, 2026
 * @author  ASAMI, Tomoharu
 */
final class SimpleEntitySpec extends AnyWordSpec
    with GivenWhenThen with Matchers with ScalaCheckDrivenPropertyChecks {
  "SimpleEntity identity and presentation" should {
    "derive the short identity from restored entropy independently of revision" in {
      forAll(Gen.chooseNum(1L, 1000000L)) { number =>
        Given("a restored entity identity and a framework-managed revision")
        val entropy = "stable" + number
        val identity = EntityId.bridgeFromParts(
          "test", "entity", EntityCollectionId("test", "entity", "sample"),
          Instant.EPOCH, entropy
        ).toOption.get
        val revision = EntityRevision.createC(number).toOption.get
        val supplied = ModelHygieneFixtures.content("owner", "body")
        When("the entity identity is exposed through the generic model")
        val result = ModelHygieneFixtures.entityView(supplied, identity, revision)
        Then("the full identity, short identity and persistence revision stay independent")
        result.id.eq(identity) shouldBe true
        result.shortid.map(_.value) shouldBe Some(entropy)
        result.revision shouldBe revision
      }
    }

    "expose absent title and content without synthesizing presentation data" in {
      Given("an entity without title or content")
      val supplied = ModelHygieneFixtures.content("owner", "body").copy(
        contentAttributes = ContentAttributes.empty
      )
      val identity = EntityId.bridgeFromParts(
        "test", "entity", EntityCollectionId("test", "entity", "sample"),
        Instant.EPOCH, "stable"
      ).toOption.get
      val entity = ModelHygieneFixtures.entityView(supplied, identity, EntityRevision.INITIAL)
      When("locale-specific presentation is requested")
      val titles = Vector(Locale.ROOT, Locale.JAPAN).map(entity.title)
      val contents = Vector(Locale.ROOT, Locale.JAPAN).map(entity.content)
      Then("titles remain empty and content remains absent for every locale")
      entity.title shouldBe ""
      titles shouldBe Vector("", "")
      contents shouldBe Vector(None, None)
    }
  }
}
