package org.simplemodeling.model.datatype

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.GivenWhenThen
import org.goldenport.record.Record

/*
 * @since   May.  1, 2026
 *  version May.  1, 2026
 * @version Jul. 28, 2026
 * @author  ASAMI, Tomoharu
 */
final class EntityIdSpec extends AnyWordSpec with Matchers with GivenWhenThen {
  "EntityId identity" should {
    "distinguish complete owning collection namespaces while retaining the physical entry key" in {
      Given("two entity IDs whose physical scalar keys are equal")
      val timestamp = Some(java.time.Instant.parse("2026-07-28T00:00:00Z"))
      val entropy = Some("same-entry")
      val application =
        EntityId(
          "single",
          "global",
          EntityCollectionId("textus", "artscene", "facility"),
          timestamp,
          entropy
        )
      val administration =
        EntityId(
          "single",
          "global",
          EntityCollectionId("textus", "administration", "facility"),
          timestamp,
          entropy
        )

      When("the IDs are compared and used as map keys")
      val entries = Map(application -> "application", administration -> "administration")

      Then("the scalar datastore key remains compatible")
      application.value shouldBe administration.value

      And("runtime identity retains both exact collection owners")
      application should not equal administration
      entries should have size 2
      entries(application) shouldBe "application"
      entries(administration) shouldBe "administration"
    }

    "compare a parsed physical identity with its exact owning collection" in {
      Given("an entity ID whose timestamp and entropy are materialized by UniversalId")
      val collection = EntityCollectionId("cncf", "builtin", "blob")
      val original = EntityId("cncf", "inline_blob", collection)

      When("the physical scalar is parsed and rebound to the exact owning collection")
      val rebound = EntityId.parse(original.value).toOption.get.copy(collection = collection)

      Then("constructor defaults and parsed materialized values describe the same identity")
      rebound shouldBe original
      rebound.hashCode shouldBe original.hashCode
    }
  }

  "EntityId ValueReader" should {
    "preserve a typed EntityId stored in a Record id field" in {
      Given("a Record whose id field already contains a typed EntityId")
      val collection = EntityCollectionId("textus_blog", "blog_component", "blog_post")
      val id = EntityId("textus_blog", "editor_post", collection)
      val record = Record.dataAuto("id" -> id)

      When("the EntityId ValueReader reads that Record")
      val result = summon[org.goldenport.convert.ValueReader[EntityId]].readC(record)

      Then("the typed identity is preserved")
      result.toOption shouldBe Some(id)
    }

    "unwrap an optional EntityId before falling back to string parsing" in {
      Given("an optional value containing a typed EntityId")
      val collection = EntityCollectionId("cncf", "builtin", "blob")
      val id = EntityId("cncf", "editor_inline_first", collection)

      When("the EntityId ValueReader reads the optional value")
      val result = summon[org.goldenport.convert.ValueReader[EntityId]].readC(Some(id))

      Then("the typed identity is unwrapped unchanged")
      result.toOption shouldBe Some(id)
    }

    "read a structured EntityId record with collection metadata" in {
      Given("a structured EntityId record with a complete collection namespace")
      val record = Record.dataAuto(
        "major" -> "textus_blog",
        "minor" -> "editor_post",
        "collection" -> Record.dataAuto(
          "major" -> "textus_blog",
          "minor" -> "blog_component",
          "name" -> "blog_post"
        )
      )

      When("the EntityId ValueReader decodes the structured record")
      val result = summon[org.goldenport.convert.ValueReader[EntityId]].readC(record).toOption.get

      Then("the entry and collection identity fields are restored")
      result.major shouldBe "textus_blog"
      result.minor shouldBe "editor_post"
      result.collection shouldBe EntityCollectionId("textus_blog", "blog_component", "blog_post")
    }

    "read a nested structured EntityId record from an id field" in {
      Given("a Record whose id field contains structured EntityId metadata")
      val record = Record.dataAuto(
        "id" -> Record.dataAuto(
          "major" -> "textus_blog",
          "minor" -> "editor_post",
          "collection" -> Record.dataAuto(
            "major" -> "textus_blog",
            "minor" -> "blog_component",
            "name" -> "blog_post"
          )
        )
      )

      When("the EntityId ValueReader decodes the enclosing Record")
      val result = summon[org.goldenport.convert.ValueReader[EntityId]].readC(record).toOption.get

      Then("the nested entry and collection identity fields are restored")
      result.major shouldBe "textus_blog"
      result.minor shouldBe "editor_post"
      result.collection shouldBe EntityCollectionId("textus_blog", "blog_component", "blog_post")
    }
  }
}
