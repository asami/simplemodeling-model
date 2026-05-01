package org.simplemodeling.model.datatype

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.goldenport.record.Record

/*
 * @since   May.  1, 2026
 * @version May.  1, 2026
 * @author  ASAMI, Tomoharu
 */
final class EntityIdSpec extends AnyWordSpec with Matchers {
  "EntityId ValueReader" should {
    "preserve a typed EntityId stored in a Record id field" in {
      val collection = EntityCollectionId("textus_blog", "blog_component", "blog_post")
      val id = EntityId("textus_blog", "editor_post", collection)
      val record = Record.dataAuto("id" -> id)

      val result = summon[org.goldenport.convert.ValueReader[EntityId]].readC(record)

      result.toOption shouldBe Some(id)
    }

    "unwrap an optional EntityId before falling back to string parsing" in {
      val collection = EntityCollectionId("cncf", "builtin", "blob")
      val id = EntityId("cncf", "editor_inline_first", collection)

      val result = summon[org.goldenport.convert.ValueReader[EntityId]].readC(Some(id))

      result.toOption shouldBe Some(id)
    }

    "read a structured EntityId record with collection metadata" in {
      val record = Record.dataAuto(
        "major" -> "textus_blog",
        "minor" -> "editor_post",
        "collection" -> Record.dataAuto(
          "major" -> "textus_blog",
          "minor" -> "blog_component",
          "name" -> "blog_post"
        )
      )

      val result = summon[org.goldenport.convert.ValueReader[EntityId]].readC(record).toOption.get

      result.major shouldBe "textus_blog"
      result.minor shouldBe "editor_post"
      result.collection shouldBe EntityCollectionId("textus_blog", "blog_component", "blog_post")
    }

    "read a nested structured EntityId record from an id field" in {
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

      val result = summon[org.goldenport.convert.ValueReader[EntityId]].readC(record).toOption.get

      result.major shouldBe "textus_blog"
      result.minor shouldBe "editor_post"
      result.collection shouldBe EntityCollectionId("textus_blog", "blog_component", "blog_post")
    }
  }
}
