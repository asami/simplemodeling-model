package org.simplemodeling.model.datatype

import java.net.{URI, URLDecoder, URLEncoder}
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Properties
import java.util.logging.{Level, LogRecord, SimpleFormatter}
import io.circe.parser.decode
import io.circe.syntax.*
import org.goldenport.record.Record
import org.scalacheck.Gen
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

/*
 * @since   May.  1, 2026
 * @version Jul. 30, 2026
 * @author  ASAMI, Tomoharu
 */
final class EntityIdSpec extends AnyWordSpec
    with GivenWhenThen
    with Matchers
    with ScalaCheckDrivenPropertyChecks {
  private val _label_character =
    Gen.oneOf(('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9') ++ Vector('_'))
  private val _label =
    for {
      first <- Gen.alphaChar
      rest <- Gen.listOf(_label_character)
    } yield s"$first${rest.mkString}"
  private val _collection =
    for {
      major <- _label
      minor <- _label
      name <- _label
    } yield EntityCollectionId(major, minor, name)
  private val _timestamp =
    Gen.chooseNum(0L, 4102444800000L).map(Instant.ofEpochMilli)
  private val _entity =
    for {
      major <- _label
      minor <- _label
      collection <- _collection
      timestamp <- _timestamp
      entropy <- _label
    } yield EntityId(major, minor, collection, Some(timestamp), Some(entropy))

  "EntityCollectionId canonical serialization" should {
    "retain an exact namespace in the established UniversalId outer grammar" in {
      Given("an EntityCollectionId with an independent namespace and logical name")
      val original = EntityCollectionId("textus", "artscene", "facility")

      When("the collection is rendered as its canonical value")
      val result = original.value

      Then("the outer UniversalId grammar carries the versioned exact collection payload")
      result shouldBe
        "textus-artscene-entity_collection-ec1_6_textus_8_artscene_8_facility-0-stable"
    }

    "round-trip every label-safe collection without delimiter guessing" in {
      forAll(_collection) { original =>
        Given("a label-safe collection whose components may contain underscores")

        When("the canonical collection value is parsed without external context")
        val parsed = EntityCollectionId.parse(original.value).toOption

        Then("every namespace component and logical name are retained exactly")
        parsed shouldBe Some(original)
      }
    }

    "reject old, mismatched, malformed, truncated, overlong, and trailing collection payloads" in {
      Given("canonical outer UniversalId values with invalid EntityCollectionId payloads")
      val invalid = Vector(
        "textus-artscene-entity_collection-facility-0-stable",
        "textus-artscene-entity_collection-ec2_6_textus_8_artscene_8_facility-0-stable",
        "other-namespace-entity_collection-ec1_6_textus_8_artscene_8_facility-0-stable",
        "textus-artscene-entity_collection-ec1_0__8_artscene_8_facility-0-stable",
        "textus-artscene-entity_collection-ec1_06_textus_8_artscene_8_facility-0-stable",
        "textus-artscene-entity_collection-ec1_x_textus_8_artscene_8_facility-0-stable",
        "textus-artscene-entity_collection-ec1_6textus_8_artscene_8_facility-0-stable",
        "textus-artscene-entity_collection-ec1_1_1_8_artscene_8_facility-0-stable",
        "textus-artscene-entity_collection-ec1_6_text-0-stable",
        "textus-artscene-entity_collection-ec1_2147483648_x-0-stable",
        "textus-artscene-entity_collection-ec1_6_textus_8_artscene_8_facility_tail-0-stable",
        "textus-artscene-entity_collection-ec1_6_textus_8_artscene_8_facility-1-other"
      )

      invalid.foreach { value =>
        When(s"the invalid collection value '$value' is parsed")
        val parsed = EntityCollectionId.parse(value).toOption

        Then("parsing fails deterministically without a fallback collection")
        parsed shouldBe None
      }
    }

    "reject invalid collection names before a non-round-trippable value can be rendered" in {
      Given("a direct and a Record collection input whose name violates the label grammar")

      When("each collection constructor boundary receives the invalid name")
      val direct = scala.util.Try(EntityCollectionId("textus", "artscene", "1facility"))
      val record = EntityCollectionId.createC(Record.dataAuto(
        "major" -> "textus",
        "minor" -> "artscene",
        "name" -> "1facility"
      )).toOption

      Then("both boundaries reject it before emitting an unparsable canonical value")
      direct.failed.toOption shouldBe defined
      record shouldBe None
    }
  }

  "AggregateCollectionId and ViewCollectionId ValueReaders" should {
    "round-trip typed and canonical values while rejecting untyped input without throwing" in {
      Given("typed aggregate and view collection identities with their canonical String values")
      val aggregate = AggregateCollectionId("textus", "artscene", "facility")
      val view = ViewCollectionId("textus", "artscene", "facility")
      val aggregatehostile = aggregate.value.stripSuffix("-0-stable") + "-1-other"
      val viewhostile = view.value.stripSuffix("-0-stable") + "-1-other"
      val aggregatereader = summon[org.goldenport.convert.ValueReader[AggregateCollectionId]]
      val viewreader = summon[org.goldenport.convert.ValueReader[ViewCollectionId]]

      When("each reader receives typed, canonical, and invalid untyped input")
      val aggregatevalues = Vector(
        aggregatereader.readC(aggregate).toOption,
        aggregatereader.readC(aggregate.value).toOption,
        aggregatereader.readC(42).toOption,
        aggregatereader.readC(aggregatehostile).toOption
      )
      val viewvalues = Vector(
        viewreader.readC(view).toOption,
        viewreader.readC(view.value).toOption,
        viewreader.readC(42).toOption,
        viewreader.readC(viewhostile).toOption
      )

      Then("canonical values round-trip and invalid untyped values become failures")
      aggregatevalues shouldBe Vector(Some(aggregate), Some(aggregate), None, None)
      viewvalues shouldBe Vector(Some(view), Some(view), None, None)
    }
  }

  "EntityId canonical serialization" should {
    "round-trip an exact collection without collection context" in {
      Given("an EntityId whose entry namespace differs from its exact collection namespace")
      val original = EntityId(
        "single",
        "global",
        EntityCollectionId("textus", "artscene", "facility"),
        Some(Instant.EPOCH),
        Some("stable")
      )

      When("the canonical String is parsed")
      val parsed = EntityId.parse(original.value).toOption

      Then("the canonical value includes the exact collection and parses as a pure inverse")
      original.value shouldBe
        "single-global-entity-ec1_6_textus_8_artscene_8_facility-0-stable"
      parsed shouldBe Some(original)
    }

    "remain deterministic and non-colliding for distinct exact collections" in {
      Given("two EntityIds with identical local fields and different exact collection namespaces")
      val timestamp = Some(Instant.parse("2026-07-29T00:00:00Z"))
      val entropy = Some("same_entry")
      val first = EntityId(
        "single",
        "global",
        EntityCollectionId("textus", "artscene", "facility"),
        timestamp,
        entropy
      )
      val second = EntityId(
        "single",
        "global",
        EntityCollectionId("textus", "administration", "facility"),
        timestamp,
        entropy
      )

      When("the canonical values are compared and used as map keys")
      val entries = Map(first -> "first", second -> "second")

      Then("complete canonical values and identity-sensitive maps retain both owners")
      first.value should not equal second.value
      first should not equal second
      entries should have size 2
      entries(first) shouldBe "first"
      entries(second) shouldBe "second"
    }

    "preserve exact identity and canonical equality for arbitrary admitted outer fields" in {
      forAll(_entity) { original =>
        Given("an EntityId with generated entry, collection, timestamp, and entropy fields")

        When("the canonical EntityId value is parsed")
        val parsed = EntityId.parse(original.value).toOption.get

        Then("parsing preserves equality, hashCode, and the exact collection")
        parsed shouldBe original
        parsed.hashCode shouldBe original.hashCode
        parsed.collection shouldBe original.collection
      }
    }

    "reject legacy and hostile scalar input without synthetic collection construction" in {
      Given("legacy and invalid outer UniversalId strings")
      val invalid = Vector(
        "single-global-entity-facility-0-stable",
        "single-global-entity-ec2_6_textus_8_artscene_8_facility-0-stable",
        "single-global-entity-ec1_1_1_8_artscene_8_facility-0-stable",
        "single-global-entity-ec1_6_textus_8_artscene_8_facility_tail-0-stable",
        "single-global-entity-ec1_6_textus_8_artscene_8_facility-0-stable-extra",
        "single-global-entity-ec1_6_textus_8_artscene_8_facility-0-unsafe-entropy"
      )

      invalid.foreach { value =>
        When(s"the scalar '$value' is parsed as an EntityId")
        val parsed = EntityId.parse(value).toOption

        Then("the parser returns no EntityId and never infers collection ownership")
        parsed shouldBe None
      }
    }

    "reject null parsing and unmaterialized, noncanonical, or delimiter-unsafe direct construction" in {
      Given("a null scalar and direct EntityId arguments outside the canonical contract")

      When("the parser and constructor boundaries receive invalid inputs")
      val nullparsed = EntityId.parse(null).toOption
      val delimiterconstruction = scala.util.Try(EntityId(
        "single",
        "global",
        EntityCollectionId("textus", "artscene", "facility"),
        Some(Instant.EPOCH),
        Some("same-entry")
      ))
      val missingtimestamp = scala.util.Try(EntityId(
        "single",
        "global",
        EntityCollectionId("textus", "artscene", "facility"),
        None,
        Some("stable")
      ))
      val missingentropy = scala.util.Try(EntityId(
        "single",
        "global",
        EntityCollectionId("textus", "artscene", "facility"),
        Some(Instant.EPOCH),
        None
      ))
      val preepochtimestamp = scala.util.Try(EntityId(
        "single",
        "global",
        EntityCollectionId("textus", "artscene", "facility"),
        Some(Instant.ofEpochMilli(-1)),
        Some("stable")
      ))
      val submillisecondtimestamp = scala.util.Try(EntityId(
        "single",
        "global",
        EntityCollectionId("textus", "artscene", "facility"),
        Some(Instant.ofEpochSecond(0, 1)),
        Some("stable")
      ))

      Then("the null parser fails and direct construction cannot create an unmaterialized, ambiguous, or non-round-trippable ID")
      nullparsed shouldBe None
      delimiterconstruction.failed.toOption shouldBe defined
      missingtimestamp.failed.toOption shouldBe defined
      missingentropy.failed.toOption shouldBe defined
      preepochtimestamp.failed.toOption shouldBe defined
      submillisecondtimestamp.failed.toOption shouldBe defined
    }

    "keep canonical values stable through Record, JSON, HTTP, form, CLI, log, and datastore boundaries" in {
      Given("an EntityId with materialized unique default outer fields and a complete canonical value")
      val before = Instant.ofEpochMilli(Instant.now().toEpochMilli)
      val original = EntityId(
        "single",
        "global",
        EntityCollectionId("textus", "artscene", "facility")
      )
      val repeated = EntityId(
        "single",
        "global",
        EntityCollectionId("textus", "artscene", "facility")
      )
      val after = Instant.ofEpochMilli(Instant.now().toEpochMilli)
      val canonical = original.value

      When("the canonical String crosses standard scalar boundary encoders")
      val record = Record.dataAuto("id" -> canonical)
      val copied = original.copy()
      val json = original.asJson.noSpaces
      val collectionjson = original.collection.asJson.noSpaces
      val encoded = URLEncoder.encode(canonical, StandardCharsets.UTF_8)
      val uri = URI.create(s"https://example.test/entities/$canonical?id=$encoded")
      val form = s"id=$encoded"
      val cli = new ProcessBuilder("entity-cli", "--id", canonical).command()
      val logrecord = new LogRecord(Level.INFO, "entity.id={0}")
      logrecord.setParameters(Array(canonical))
      val log = new SimpleFormatter().format(logrecord)
      val datastore = new Properties()
      datastore.setProperty(canonical, "stored")
      val reader = summon[org.goldenport.convert.ValueReader[EntityId]]
      val recorddecoded = reader.readC(record).toOption
      val jsondecoded = decode[EntityId](json)
      val collectiondecoded = decode[EntityCollectionId](collectionjson)

      Then("every boundary preserves the same canonical String without an application wrapper")
      recorddecoded shouldBe Some(original)
      original.timestamp.map(value => Instant.ofEpochMilli(value.toEpochMilli)) shouldBe original.timestamp
      original.timestamp.exists(value => !value.isBefore(before) && !value.isAfter(after)) shouldBe true
      original.entropy shouldBe defined
      repeated should not equal original
      repeated.entropy should not equal original.entropy
      copied shouldBe original
      json shouldBe s"\"$canonical\""
      jsondecoded shouldBe Right(original)
      collectionjson shouldBe s"\"${original.collection.value}\""
      collectiondecoded shouldBe Right(original.collection)
      URLDecoder.decode(encoded, StandardCharsets.UTF_8) shouldBe canonical
      uri.getRawPath.stripPrefix("/entities/") shouldBe canonical
      uri.getRawQuery.stripPrefix("id=") shouldBe encoded
      form.stripPrefix("id=") shouldBe encoded
      cli.get(2) shouldBe canonical
      log should include(canonical)
      datastore.getProperty(canonical) shouldBe "stored"
    }
  }

  "EntityId ValueReader" should {
    "preserve typed and structured EntityIds without creating a second scalar contract" in {
      Given("typed and structured Record inputs carrying a complete exact collection")
      val collection = EntityCollectionId("textus_blog", "blog_component", "blog_post")
      val typed = EntityId("textus_blog", "editor_post", collection)
      val structured = Record.dataAuto(
        "id" -> Record.dataAuto(
          "major" -> "textus_blog",
          "minor" -> "editor_post",
          "collection" -> Record.dataAuto(
            "major" -> "textus_blog",
            "minor" -> "blog_component",
            "name" -> "blog_post"
          ),
          "timestamp" -> Instant.EPOCH.toString,
          "entropy" -> "stable"
        )
      )

      When("the EntityId ValueReader decodes both forms")
      val reader = summon[org.goldenport.convert.ValueReader[EntityId]]
      val fromtyped = reader.readC(Record.dataAuto("id" -> typed)).toOption
      val fromstructured = reader.readC(structured).toOption

      Then("both retain the exact collection identity")
      fromtyped shouldBe Some(typed)
      fromstructured.map(_.collection) shouldBe Some(collection)
    }

    "reject an incomplete structured record instead of fabricating collection ownership" in {
      Given("a structured Record with entry namespace and only a collection name")
      val incomplete = Record.dataAuto(
        "major" -> "single",
        "minor" -> "global",
        "collectionName" -> "facility"
      )

      When("the EntityId ValueReader decodes the Record")
      val result = summon[org.goldenport.convert.ValueReader[EntityId]].readC(incomplete).toOption

      Then("the reader rejects the incomplete record without deriving an owner from entry fields")
      result shouldBe None
    }

    "reject structured records that omit or corrupt canonical outer fields" in {
      Given("complete entry and collection fields with missing or malformed timestamp and entropy values")
      val base = Record.dataAuto(
        "major" -> "single",
        "minor" -> "global",
        "collection" -> Record.dataAuto(
          "major" -> "textus",
          "minor" -> "artscene",
          "name" -> "facility"
        )
      )
      val missingtimestamp = base
      val malformedtimestamp = Record.dataAuto(
        "major" -> "single",
        "minor" -> "global",
        "collection" -> EntityCollectionId("textus", "artscene", "facility").value,
        "timestamp" -> "not-an-instant",
        "entropy" -> "stable"
      )
      val missingentropy = Record.dataAuto(
        "major" -> "single",
        "minor" -> "global",
        "collection" -> EntityCollectionId("textus", "artscene", "facility").value,
        "timestamp" -> Instant.EPOCH.toString
      )
      val malformedentropy = Record.dataAuto(
        "major" -> "single",
        "minor" -> "global",
        "collection" -> EntityCollectionId("textus", "artscene", "facility").value,
        "timestamp" -> Instant.EPOCH.toString,
        "entropy" -> "same-entry"
      )
      val preepochtimestamp = Record.dataAuto(
        "major" -> "single",
        "minor" -> "global",
        "collection" -> EntityCollectionId("textus", "artscene", "facility").value,
        "timestamp" -> Instant.ofEpochMilli(-1).toString,
        "entropy" -> "stable"
      )
      val submillisecondtimestamp = Record.dataAuto(
        "major" -> "single",
        "minor" -> "global",
        "collection" -> EntityCollectionId("textus", "artscene", "facility").value,
        "timestamp" -> Instant.ofEpochSecond(0, 1).toString,
        "entropy" -> "stable"
      )

      When("the EntityId ValueReader decodes each structured record")
      val reader = summon[org.goldenport.convert.ValueReader[EntityId]]
      val results = Vector(
        missingtimestamp,
        malformedtimestamp,
        missingentropy,
        malformedentropy,
        preepochtimestamp,
        submillisecondtimestamp
      )
        .map(reader.readC(_).toOption)

      Then("decoding rejects every record without generating a current timestamp or entropy")
      results shouldBe Vector(None, None, None, None, None, None)
    }
  }
}
