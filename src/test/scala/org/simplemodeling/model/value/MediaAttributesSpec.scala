package org.simplemodeling.model.value

import org.scalacheck.Gen
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import org.simplemodeling.model.ModelHygieneFixtures
import org.goldenport.Consequence
import org.simplemodeling.model.value.internal.ProjectionValueReaders

/*
 * @since   Dec. 22, 2025
 *  version Mar. 29, 2026
 * @version Sep. 28, 2026
 * @author  ASAMI, Tomoharu
 */
final class MediaAttributesSpec extends AnyWordSpec
    with GivenWhenThen with Matchers with ScalaCheckDrivenPropertyChecks {
  "MediaAttributes projection" should {
    "preserve each media collection's supplied order and repetitions" in {
      forAll(Gen.chooseNum(0, 8)) { count =>
        Given("ordered image, audio, video and attachment collections")
        val metadata = ModelHygieneFixtures.content("owner", "body")
        val ordered = Vector.tabulate(count)(index => SecurityAttributes.ownedBy("asset" + index).ownerId)
        val identities = ordered ++ ordered.take(1)
        val supplied = MediaAttributes(
          None, identities.map(Image(_, metadata)), identities.map(Audio(_, metadata)),
          identities.map(Video(_, metadata)), identities.map(Attachment(_, metadata))
        )
        When("the media projection is read")
        val result = ProjectionValueReaders.mediaAttributes.readC(supplied).toOption.get
        Then("each collection retains its identities in input order")
        result.eq(supplied) shouldBe true
        result.images.map(_.id) shouldBe identities
        result.audios.map(_.id) shouldBe identities
        result.videos.map(_.id) shouldBe identities
        result.atathments.map(_.id) shouldBe identities
      }
    }

    "reject incompatible scalar projection input without inventing empty media" in {
      forAll(Gen.alphaStr) { text =>
        Given("a scalar string instead of a MediaAttributes value")
        val supplied: Any = text
        When("the media projection reader interprets it")
        val result = ProjectionValueReaders.mediaAttributes.readC(supplied)
        Then("the scalar is rejected even when the string is empty")
        result shouldBe a[Consequence.Failure[_]]
      }
    }
  }
}
