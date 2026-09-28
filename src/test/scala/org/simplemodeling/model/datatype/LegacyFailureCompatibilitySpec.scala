package org.simplemodeling.model.datatype

import org.goldenport.{Conclusion, Consequence}
import org.goldenport.convert.ValueReader
import org.goldenport.record.Record
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

/*
 * @since   Sep. 28, 2026
 * @version Sep. 28, 2026
 */
final class LegacyFailureCompatibilitySpec extends AnyWordSpec
    with GivenWhenThen with Matchers {
  "Identity validation diagnostics" should {
    "retain the legacy null-input diagnostics for every collection identity" in {
      val scenarios = Vector[(String, () => Consequence[Any])](
        "EntityId" -> (() => summon[ValueReader[EntityId]].readC(null)),
        "EntityCollectionId" -> (() => summon[ValueReader[EntityCollectionId]].readC(null)),
        "AggregateCollectionId" -> (() => summon[ValueReader[AggregateCollectionId]].readC(null)),
        "ViewCollectionId" -> (() => summon[ValueReader[ViewCollectionId]].readC(null))
      )
      scenarios.foreach { case (kind, read) =>
        Given(s"a null input at the $kind value-reader boundary")
        val expected = Conclusion.simple(s"Invalid $kind value: null")
        When("the identity is interpreted")
        val result = read()
        Then("the existing message and failure classification remain unchanged")
        _same_diagnostic(result, expected)
      }
    }

    "retain the existing diagnostics for every missing structured identity field" in {
      val entityfields: Vector[(String, Any)] = Vector(
        "major" -> "test", "minor" -> "entity",
        "collection" -> EntityCollectionId("test", "entity", "sample"),
        "timestamp" -> "1970-01-01T00:00:00Z", "entropy" -> "stable"
      )
      val collectionfields: Vector[(String, Any)] = Vector(
        "major" -> "test", "minor" -> "entity", "name" -> "sample"
      )
      val scenarios = entityfields.map { case (key, _) =>
        val label = if (key == "collection") "complete collection" else key
        ("EntityId", label, () => EntityId.createC(Record.dataAuto(entityfields.filterNot(_._1 == key)*)))
      } ++ collectionfields.map { case (key, _) =>
        ("EntityCollectionId", key, () => EntityCollectionId.createC(Record.dataAuto(collectionfields.filterNot(_._1 == key)*)))
      }
      scenarios.foreach { case (kind, key, read) =>
        Given(s"a $kind record missing $key")
        val expected = Conclusion.simple(s"Invalid $kind record: missing $key")
        When("the structured identity is interpreted")
        val result = read()
        Then("the same legacy diagnostic is returned without new metadata")
        _same_diagnostic(result, expected)
      }
    }

    "retain the existing empty complete-identity diagnostic" in {
      Given("a record with an empty complete EntityId value")
      val record = Record.dataAuto("id" -> "")
      val expected = Conclusion.simple("Invalid EntityId value: empty")
      When("the record is interpreted as an EntityId")
      val result = EntityId.createC(record)
      Then("the existing empty-value failure classification and message are retained")
      _same_diagnostic(result, expected)
    }
  }

  private def _same_diagnostic(result: Consequence[Any], expected: Conclusion): org.scalatest.Assertion =
    result match {
      case Consequence.Failure(actual) =>
        actual.displayMessage shouldBe expected.displayMessage
        actual.status shouldBe expected.status
        actual.observation.taxonomy shouldBe expected.observation.taxonomy
        actual.interpretation shouldBe expected.interpretation
        actual.disposition shouldBe expected.disposition
        actual.previous shouldBe expected.previous
      case _ => fail("The invalid identity unexpectedly succeeded")
    }
}
