package org.simplemodeling.model.datatype

import java.time.Instant
import io.circe.{Codec, Decoder, Encoder}
import io.circe.parser.decode
import io.circe.syntax.*
import org.goldenport.Consequence
import org.goldenport.id.CompactUuid
import org.goldenport.record.Record
import org.scalacheck.Gen
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

/*
 * @since   May.  1, 2026
 * @version Sep. 16, 2026
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
    } yield EntityId.restore(major, minor, collection, timestamp, entropy).toOption.get

  private final class FacilityId private (
    major: String,
    minor: String,
    timestamp: Instant,
    entropy: String
  ) extends EntityId(major, minor, FacilityId.collection, Some(timestamp), Some(entropy))

  private object FacilityId {
    val collection = EntityCollectionId("textus", "artscene", "facility")

    def issue(major: String, minor: String): FacilityId =
      new FacilityId(
        major,
        minor,
        Instant.ofEpochMilli(Instant.now().toEpochMilli),
        CompactUuid.generateString()
      )

    def restore(value: String): Consequence[FacilityId] =
      EntityId.parse(value).flatMap(_from_generic)

    def restore(record: Record): Consequence[FacilityId] =
      EntityId.createC(record).flatMap(_from_generic)

    def bridgeFromParts(
      major: String,
      minor: String,
      timestamp: Instant,
      entropy: String
    ): Consequence[FacilityId] =
      EntityId.bridgeFromParts(major, minor, collection, timestamp, entropy).flatMap(_from_generic)

    given Codec[FacilityId] = Codec.from(
      Decoder.decodeString.emap(value => restore(value).toOption.toRight(s"Invalid FacilityId value: '$value'")),
      Encoder.encodeString.contramap(_.value)
    )

    private def _from_generic(value: EntityId): Consequence[FacilityId] =
      if (value.collection != collection)
        Consequence.valueInvalid("FacilityId requires its expected complete collection")
      else
        Consequence.success(new FacilityId(value.major, value.minor, value.timestamp.get, value.entropy.get))
  }

  private final class VenueId private (
    major: String,
    minor: String,
    timestamp: Instant,
    entropy: String
  ) extends EntityId(major, minor, VenueId.collection, Some(timestamp), Some(entropy))

  private object VenueId {
    val collection = EntityCollectionId("textus", "artscene", "venue")

    def issue(major: String, minor: String): VenueId =
      new VenueId(
        major,
        minor,
        Instant.ofEpochMilli(Instant.now().toEpochMilli),
        CompactUuid.generateString()
      )
  }

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

  "EntityId inheritance and restoration" should {
    "materialize ordinary identities only through concrete subtypes" in {
      Given("two concrete entity identity subtypes with distinct complete collections")
      val before = Instant.ofEpochMilli(Instant.now().toEpochMilli)

      When("each concrete factory issues an identity")
      val facility = FacilityId.issue("single", "global")
      val venue = VenueId.issue("single", "global")
      val after = Instant.ofEpochMilli(Instant.now().toEpochMilli)
      val generic: Vector[EntityId] = Vector(facility, venue)
      val issuedtimestamp = facility.timestamp
      val issuedentropy = facility.entropy
      val firstvalue = facility.value
      val repeatedvalue = facility.value

      Then("both inhabit the abstract EntityId boundary without becoming interchangeable")
      facility shouldBe a[EntityId]
      venue shouldBe a[EntityId]
      generic.map(_.collection) shouldBe Vector(FacilityId.collection, VenueId.collection)
      facility.value should not equal venue.value

      And("ordinary issuance materializes timestamp and entropy once and keeps identity immutable")
      facility.timestamp.exists(value => !value.isBefore(before) && !value.isAfter(after)) shouldBe true
      facility.entropy shouldBe defined
      firstvalue shouldBe repeatedvalue
      facility.timestamp shouldBe issuedtimestamp
      facility.entropy shouldBe issuedentropy
      facility.hashCode shouldBe facility.hashCode
    }

    "round-trip generic restoration without inferring a domain subtype" in {
      Given("a canonical FacilityId whose entry namespace differs from its collection namespace")
      val original = FacilityId.bridgeFromParts("single", "global", Instant.EPOCH, "stable").toOption.get

      When("the generic parser restores its complete canonical value")
      val parsed = EntityId.parse(original.value).toOption

      Then("the generic boundary retains exact identity but does not claim the FacilityId subtype")
      original.value shouldBe "single-global-entity-ec1_6_textus_8_artscene_8_facility-0-stable"
      parsed shouldBe Some(original)
      parsed.map(_.getClass) should not equal Some(original.getClass)
      parsed.map(_.collection) shouldBe Some(FacilityId.collection)
    }

    "restore typed and generic canonical Record and JSON values with complete collection checks" in {
      Given("a deterministic FacilityId and a different concrete VenueId")
      val facility = FacilityId.bridgeFromParts("single", "global", Instant.EPOCH, "stable").toOption.get
      val venue = VenueId.issue("single", "global")
      val record = Record.dataAuto("id" -> facility.value)
      val generic: EntityId = facility
      val genericjson = generic.asJson.noSpaces
      val typedjson = facility.asJson.noSpaces

      When("generic and typed restoration paths receive canonical, Record, and JSON forms")
      val genericparsed = EntityId.parse(facility.value).toOption
      val genericrecord = EntityId.createC(record).toOption
      val genericdecoded = decode[EntityId](genericjson)
      val typedparsed = FacilityId.restore(facility.value).toOption
      val typedrecord = FacilityId.restore(record).toOption
      val typeddecoded = decode[FacilityId](typedjson)
      val rejected = FacilityId.restore(venue.value).toOption

      Then("generic transport preserves canonical identity and typed transport restores only its declared collection")
      genericparsed shouldBe Some(facility)
      genericrecord shouldBe Some(facility)
      genericdecoded shouldBe Right(facility)
      typedparsed shouldBe Some(facility)
      typedrecord shouldBe Some(facility)
      typeddecoded shouldBe Right(facility)
      rejected shouldBe None
    }

    "retain explicit restoration and special bridge boundaries without synthetic entropy" in {
      Given("complete canonical identity parts including a fixed timestamp and entropy")
      val collection = EntityCollectionId("textus", "administration", "facility")

      When("the generic restore and explicit bridge operations materialize those supplied parts")
      val restored = EntityId.restore("single", "global", collection, Instant.EPOCH, "stable").toOption
      val bridged = EntityId.bridgeFromParts("single", "global", collection, Instant.EPOCH, "stable").toOption

      Then("both recovery-oriented operations retain exact deterministic fields without ordinary issuance")
      restored shouldBe bridged
      restored.map(_.value) shouldBe Some("single-global-entity-ec1_6_textus_14_administration_8_facility-0-stable")
      restored.map(_.timestamp) shouldBe Some(Some(Instant.EPOCH))
      restored.map(_.entropy) shouldBe Some(Some("stable"))
    }

    "preserve exact identity and canonical equality for arbitrary admitted restored fields" in {
      forAll(_entity) { original =>
        Given("a generic restored EntityId with complete outer and collection fields")

        When("the canonical EntityId value is parsed")
        val parsed = EntityId.parse(original.value).toOption.get

        Then("parsing preserves equality, hashCode, and the exact collection")
        parsed shouldBe original
        parsed.hashCode shouldBe original.hashCode
        parsed.collection shouldBe original.collection
      }
    }

    "reject legacy hostile and incomplete recovery input without synthetic collection construction" in {
      Given("legacy scalar values and invalid explicit recovery parts")
      val invalid = Vector(
        "single-global-entity-facility-0-stable",
        "single-global-entity-ec2_6_textus_8_artscene_8_facility-0-stable",
        "single-global-entity-ec1_1_1_8_artscene_8_facility-0-stable",
        "single-global-entity-ec1_6_textus_8_artscene_8_facility_tail-0-stable",
        "single-global-entity-ec1_6_textus_8_artscene_8_facility-0-stable-extra",
        "single-global-entity-ec1_6_textus_8_artscene_8_facility-0-unsafe-entropy"
      )
      val collection = EntityCollectionId("textus", "artscene", "facility")

      When("generic parsing and restoration receive malformed or noncanonical input")
      val parsed = invalid.map(EntityId.parse(_).toOption)
      val nullparsed = EntityId.parse(null).toOption
      val delimiterbridge = EntityId.bridgeFromParts("single", "global", collection, Instant.EPOCH, "same-entry").toOption
      val preepochrestore = EntityId.restore("single", "global", collection, Instant.ofEpochMilli(-1), "stable").toOption
      val submillisecondrestore = EntityId.restore("single", "global", collection, Instant.ofEpochSecond(0, 1), "stable").toOption

      Then("every rejected input fails without issuing an ID or deriving collection ownership")
      parsed shouldBe Vector.fill(invalid.size)(None)
      nullparsed shouldBe None
      delimiterbridge shouldBe None
      preepochrestore shouldBe None
      submillisecondrestore shouldBe None
    }
  }

  "EntityId ValueReader" should {
    "preserve typed and structured EntityIds without creating a second scalar contract" in {
      Given("typed and structured Record inputs carrying a complete exact collection")
      val collection = EntityCollectionId("textus_blog", "blog_component", "blog_post")
      val typed = EntityId.bridgeFromParts(
        "textus_blog",
        "editor_post",
        collection,
        Instant.EPOCH,
        "stable"
      ).toOption.get
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
