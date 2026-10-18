package org.simplemodeling.model.value

import java.math.MathContext
import java.net.URL
import java.nio.charset.StandardCharsets
import java.time.{Instant, LocalDate, LocalDateTime, OffsetDateTime, ZonedDateTime}
import java.util.{Locale, TimeZone}
import cats.data.NonEmptyVector
import io.circe.Json
import org.goldenport.Consequence
import org.goldenport.datatype.{Identifier, ObjectId}
import org.goldenport.record.{Record, RecordPresentable}
import org.scalacheck.{Gen, Prop, Test}
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.simplemodeling.model.ModelHygieneFixtures
import org.simplemodeling.model.datatype.EntityRevision
import org.simplemodeling.model.directive.{Condition, Update}
import org.simplemodeling.model.powertype.Powertype
import org.simplemodeling.model.statemachine.{ActivationStatus, Aliveness, PostStatus, StateMachine}

/*
 * @since   Oct. 18, 2026
 * @version Oct. 18, 2026
 */
final class ModelValueEncoderSpec extends AnyWordSpec with Matchers with GivenWhenThen {
  import ModelValueEncoderSpec.*

  "ModelValueEncoder" should {
    "project nominal values" which {
      "preserves primitives, exact numbers, Json and Record instead of wrapper presentation" in {
        Given("nominal payloads including an explicit competing Record presentation")
        val decimal = new BigDecimal(new java.math.BigDecimal("123456789012345678901234567890.1234567890123456789"), MathContext.UNLIMITED)
        val json = Json.obj("null" -> Json.Null, "positions" -> Json.arr(Json.Null))
        val record = Record.data("raw" -> json, "empty" -> Record.empty)
        val values = Vector[Any]("text", false, 1.toByte, 2.toShort, 3, Long.MaxValue,
          1.25f, 2.5d, BigInt("123456789012345678901234567890"), decimal,
          new java.math.BigInteger("123456789012345678901234567890"),
          new java.math.BigDecimal("0.00000000000000000000000000001"), json, record)
        When("the public model boundary projects each nominal scalar")
        val results = values.map(value => ModelValueEncoder.encodeC(Scalar(value)))
        Then("the underlying values and runtime types survive without a synthetic value Record")
        results.zip(values).foreach { case (result, value) =>
          result.toOption shouldBe Some(value)
          result.toOption.get.getClass shouldBe value.getClass
        }
        results.takeRight(2).map(_.toOption.get.asInstanceOf[AnyRef]).zip(Vector(json, record)).foreach {
          case (result, value) => result should be theSameInstanceAs value
        }
        results(9).toOption.get.asInstanceOf[BigDecimal].bigDecimal.toPlainString shouldBe
          "123456789012345678901234567890.1234567890123456789"
      }

      "distinguishes nominal persistence revisions from generated structured VALUE objects" in {
        Given("a nominal persistence revision and country/postal classes declared under CML VALUE")
        val revision = EntityRevision.createC(Long.MaxValue).toOption.get
        val country = CountryCode("JP")
        val postal = PostalCode("160-0022")
        When("the public encoder projects these model-owned nominal values")
        val results = Vector[Any](revision, country, postal).map(ModelValueEncoder.encodeC)
        Then("the nominal revision stays scalar while declared VALUE objects retain their Record shape")
        revision shouldBe a[NominalScalar]
        country should not be a[NominalScalar]
        postal should not be a[NominalScalar]
        results.map(_.toOption) shouldBe Vector(Some(Long.MaxValue),
          Some(Record.data("value" -> "JP")), Some(Record.data("value" -> "160-0022")))
      }
    }

    "project ordered attribute records" which {
      "retains literal lifecycle field order and independent lifecycle values" in {
        Given("distinct lifecycle timestamps, principals and nondefault states")
        val supplied = LifecycleAttributes(Instant.parse("2026-01-02T03:04:05Z"),
          Instant.parse("2026-02-03T04:05:06Z"), Identifier("creator"), Identifier("editor"),
          PostStatus.Published, Aliveness.Suspended)
        When("the public boundary projects lifecycle attributes")
        val result = ModelValueEncoder.encodeC(supplied)
        Then("all six external values appear in their declared order")
        _pairs(result.toOption.get) shouldBe Vector(
          "createdAt" -> "2026-01-02T03:04:05Z", "updatedAt" -> "2026-02-03T04:05:06Z",
          "createdBy" -> "creator", "updatedBy" -> "editor", "postStatus" -> "published", "aliveness" -> "suspended")
      }

      "retains all publication timestamps in literal external order" in {
        Given("five independently identified publication timestamps")
        val supplied = PublicationAttributes(Some(ZonedDateTime.parse("2026-01-01T00:00Z")),
          Some(ZonedDateTime.parse("2026-02-01T00:00Z")), Some(ZonedDateTime.parse("2026-03-01T00:00Z")),
          Some(ZonedDateTime.parse("2026-04-01T00:00Z")), Some(ZonedDateTime.parse("2026-05-01T00:00Z")))
        When("the public boundary projects publication attributes")
        val result = ModelValueEncoder.encodeC(supplied)
        Then("the timestamp values retain publish, public, close, start and end meaning")
        _pairs(result.toOption.get) shouldBe Vector("publishAt" -> "2026-01-01T00:00Z",
          "publicAt" -> "2026-02-01T00:00Z", "closeAt" -> "2026-03-01T00:00Z",
          "startAt" -> "2026-04-01T00:00Z", "endAt" -> "2026-05-01T00:00Z")
      }

      "omits absent publication fields at Record construction while retaining present timestamps" in {
        Given("publication attributes with absent fields surrounding a present close date")
        val supplied = PublicationAttributes(None, None, Some(ZonedDateTime.parse("2026-03-01T00:00Z")), None, None)
        When("the public boundary projects optional publication values")
        val result = ModelValueEncoder.encodeC(supplied)
        Then("ordinary Record omission leaves only the close field")
        _pairs(result.toOption.get) shouldBe Vector("closeAt" -> "2026-03-01T00:00Z")
      }

      "uses external camelCase security fields and ordered nested Boolean permissions" in {
        Given("distinct security identities and three different permission combinations")
        val owner = SecurityAttributes.Rights.Permissions(true, false, true)
        val group = SecurityAttributes.Rights.Permissions(false, true, false)
        val other = SecurityAttributes.Rights.Permissions(false, false, true)
        val rights = SecurityAttributes.Rights(owner, group, other)
        val supplied = SecurityAttributes(ObjectId(Identifier("owner")), ObjectId(Identifier("group")),
          rights, ObjectId(Identifier("privilege")))
        When("the public boundary projects security, rights and each standalone permission value")
        val result = ModelValueEncoder.encodeC(supplied)
        val rightsresult = ModelValueEncoder.encodeC(rights)
        val permissionresults = Vector(owner, group, other).map(ModelValueEncoder.encodeC)
        Then("the external representation keeps literal order and every Boolean without using storage names")
        val expectedrights = Record.data("owner" -> _permissions(true, false, true),
          "group" -> _permissions(false, true, false), "other" -> _permissions(false, false, true))
        _pairs(result.toOption.get) shouldBe Vector("ownerId" -> "owner", "groupId" -> "group",
          "rights" -> expectedrights, "privilegeId" -> "privilege")
        _pairs(rightsresult.toOption.get) shouldBe expectedrights.fields.map(field => field.key -> field.value.single)
        permissionresults.map(result => _pairs(result.toOption.get)) shouldBe Vector(
          Vector("read" -> true, "write" -> false, "execute" -> true),
          Vector("read" -> false, "write" -> true, "execute" -> false),
          Vector("read" -> false, "write" -> false, "execute" -> true))
      }

      "retains resource dates and state in literal external order" in {
        Given("three resource dates and an expired activation state")
        val supplied = ResourceAttributes(Some(ZonedDateTime.parse("2026-01-01T00:00Z")),
          Some(ZonedDateTime.parse("2026-02-01T00:00Z")), Some(ZonedDateTime.parse("2026-03-01T00:00Z")),
          ActivationStatus.Expired)
        When("the public boundary projects resource attributes")
        val result = ModelValueEncoder.encodeC(supplied)
        Then("the external values identify each date and the explicit state presentation")
        _pairs(result.toOption.get) shouldBe Vector("activatedAt" -> "2026-01-01T00:00Z",
          "deactivatedAt" -> "2026-02-01T00:00Z", "expiresAt" -> "2026-03-01T00:00Z", "activationStatus" -> "expired")
      }

      "keeps empty attribute records and present empty media children" in {
        Given("empty audit and contextual groups and media without a URL or members")
        val supplied = Vector[Any](AuditAttributes(), ContextualAttributes(),
          MediaAttributes(None, Vector.empty, Vector.empty, Vector.empty, Vector.empty), ResourceAttributes())
        When("the public boundary projects these empty groups")
        val results = supplied.map(ModelValueEncoder.encodeC)
        Then("empty Records and collections remain present while optional dates and URL are omitted")
        results.take(2).map(_.toOption) shouldBe Vector(Some(Record.empty), Some(Record.empty))
        _pairs(results(2).toOption.get) shouldBe Vector("images" -> Vector.empty,
          "audios" -> Vector.empty, "videos" -> Vector.empty, "attachments" -> Vector.empty)
        _pairs(results(3).toOption.get) shouldBe Vector("activationStatus" -> "inactive")
      }

      "projects a complete composed content value using independent nested expected records" in {
        Given("the existing complete model fixture with a concrete owner and document body")
        val supplied = ModelHygieneFixtures.content("owner", "document body")
        When("the public boundary projects the ten composed attribute groups")
        val result = ModelValueEncoder.encodeC(supplied)
        Then("the exact field order and all nested content agree with the external contract")
        result.toOption shouldBe Some(_expected_content("owner", "document body"))
        _pairs(result.toOption.get).map(_._1) shouldBe Vector("nameAttributes", "descriptiveAttributes",
          "contentAttributes", "lifecycleAttributes", "publicationAttributes", "securityAttributes",
          "resourceAttributes", "auditAttributes", "mediaAttributes", "contextualAttribute")
      }

      "projects every actual media wrapper and preserves collection order and repetitions" in {
        Given("actual image, audio, video and attachment wrappers over the existing content fixture")
        val content = ModelHygieneFixtures.content("owner", "asset body")
        val first = ObjectId(Identifier("first"))
        val second = ObjectId(Identifier("second"))
        val images = Vector(Image(second, content), Image(first, content), Image(second, content))
        val audio = Audio(first, content)
        val video = Video(second, content)
        val attachment = Attachment(first, content)
        val supplied = MediaAttributes(Some(new URL("https://example.org/media?q=1#asset")), images,
          Vector(audio), Vector(video), Vector(attachment))
        When("the public boundary projects media and each standalone wrapper")
        val result = ModelValueEncoder.encodeC(supplied)
        val wrappers = Vector[Any](images.head, audio, video, attachment).map(ModelValueEncoder.encodeC)
        Then("attachments uses the external spelling and all wrapper identities and content remain ordered")
        val expectedcontent = _expected_content("owner", "asset body")
        val expectedfirst = Record.data("id" -> "first", "simpleobject" -> expectedcontent)
        val expectedsecond = Record.data("id" -> "second", "simpleobject" -> expectedcontent)
        _pairs(result.toOption.get) shouldBe Vector("url" -> "https://example.org/media?q=1#asset",
          "images" -> Vector(expectedsecond, expectedfirst, expectedsecond), "audios" -> Vector(expectedfirst),
          "videos" -> Vector(expectedsecond), "attachments" -> Vector(expectedfirst))
        wrappers.map(_.toOption) shouldBe Vector(Some(expectedsecond), Some(expectedfirst), Some(expectedsecond), Some(expectedfirst))
        wrappers.foreach(result => _pairs(result.toOption.get).map(_._1) shouldBe Vector("id", "simpleobject"))
      }
    }

    "retain directive intent" which {
      "projects conditions without changing their acceptance semantics" in {
        Given("unrestricted, scalar-equality and candidate-set conditions over nominal values")
        val unrestricted = Condition.Any
        val equal = Condition.Is(Scalar("chosen"))
        val candidates = Condition.In(Set(Scalar("alpha"), Scalar("beta")))
        When("the public boundary projects each supported condition")
        val anyresult = ModelValueEncoder.encodeC(unrestricted)
        val equalresult = ModelValueEncoder.encodeC(equal)
        val inresult = ModelValueEncoder.encodeC(candidates)
        Then("Any means absence, Is means its scalar and In retains candidate membership")
        anyresult.toOption shouldBe Some(None)
        equalresult.toOption shouldBe Some("chosen")
        inresult.toOption.get.asInstanceOf[Vector[String]].toSet shouldBe Set("alpha", "beta")
        equal.accepts(Scalar("chosen")) shouldBe true
        candidates.accepts(Scalar("gamma")) shouldBe false
      }

      "keeps clear and noop markers distinct until an encoded patch is interpreted" in {
        Given("a patch containing Noop, typed clear and an exact nominal assignment")
        val number = BigInt("123456789012345678901234567890")
        val supplied = Vector[Any](Update.Noop, Update.SetNull, Update.SetValue(Scalar(number)))
        When("the public boundary encodes values before a patch is passed to the actual changes interpreter")
        val encoded = supplied.map(value => ModelValueEncoder.encodeC(value).toOption.get)
        val patch = Record.data("untouched" -> encoded(0), "clear" -> encoded(1), "assigned" -> encoded(2))
        val changes = Update.toChangesRecord(patch)
        Then("Noop omits, clear remains typed and assignment unwraps to the exact projected value")
        encoded shouldBe Vector(Update.Noop, Update.SetNull, Update.SetValue(number))
        _pairs(changes) shouldBe Vector("clear" -> Update.SetNull, "assigned" -> number)
        changes.getAny("clear").get.asInstanceOf[AnyRef] should be theSameInstanceAs Update.SetNull
      }

      "recursively projects structured SetValue payloads without executing updates" in {
        Given("a required assignment containing model attributes and an explicit absent assignment")
        val supplied = Update.SetValue(ModelHygieneFixtures.content("owner", "replacement"))
        When("the public boundary projects both assignments")
        val result = ModelValueEncoder.encodeC(supplied)
        val absent = ModelValueEncoder.encodeC(Update.SetValue(None))
        Then("the directive wrapper retains the projected content and absence is never an implicit clear")
        result.toOption shouldBe Some(Update.SetValue(_expected_content("owner", "replacement")))
        absent.toOption shouldBe Some(Update.SetValue(None))
      }

      "rejects native null required assignments rather than manufacturing clear intent" in {
        Given("a required native-null assignment and a native-null equality condition")
        val supplied = Vector[Any](Update.SetValue(null), Condition.Is(null), Scalar(null))
        When("the public boundary recursively projects the required payloads")
        val results = supplied.map(ModelValueEncoder.encodeC)
        Then("each projection fails through Consequence and none becomes SetNull or absence")
        results.foreach(_ shouldBe a[Consequence.Failure[_]])
      }
    }

    "delegate Core traversal and presentation" which {
      "gives named scalar and state meaning precedence over competing Record presentation" in {
        Given("a powertype and state with exact print strings and forbidden Record callbacks")
        val power = RecordPower(" selected 雪 ")
        val state = RecordState(" active 雪 ")
        val supplied = Vector[Any](power, state, power)
        When("the public boundary projects both named values and constructs a nested ordered Record")
        val powerresult = ModelValueEncoder.encodeC(power)
        val stateresult = ModelValueEncoder.encodeC(state)
        val nestedresult = ModelValueEncoder.encodeC(Vector[Any](supplied, state)).map { values =>
          Record.dataAuto("values" -> values, "state" -> " active 雪 ")
        }
        Then("literal print strings retain whitespace, Unicode, order and duplicates without storage or Record fallback")
        powerresult.toOption shouldBe Some(" selected 雪 ")
        stateresult.toOption shouldBe Some(" active 雪 ")
        nestedresult.toOption shouldBe Some(Record.data(
          "values" -> Vector(Vector(" selected 雪 ", " active 雪 ", " selected 雪 "), " active 雪 "),
          "state" -> " active 雪 "))
      }

      "fails null and throwing print callbacks without competing Record fallback" in {
        Given("null powertype and state print values and a throwing print callback, all with forbidden Record callbacks")
        val supplied = Vector[Any](RecordPower(null), RecordState(null), ThrowingPrintPower())
        When("the public boundary projects each invalid named presentation")
        val results = supplied.map(ModelValueEncoder.encodeC)
        Then("each projection is a Consequence failure rather than an exception or successful alternative representation")
        results.foreach(_ shouldBe a[Consequence.Failure[_]])
      }

      "preserves native ordered collections, duplicate model values and nonempty cardinality" in {
        Given("ordered duplicate nominal values in Seq, Array and actual Cats NonEmptyVector")
        val supplied = Vector[Any](List(Scalar("b"), Scalar("a"), Scalar("b")),
          Array(Scalar("b"), Scalar("a"), Scalar("b")), NonEmptyVector.of(Scalar("b"), Scalar("a"), Scalar("b")))
        When("the public boundary delegates traversal with its model adapter")
        val results = supplied.map(ModelValueEncoder.encodeC)
        Then("all representations retain order and repeated scalar values")
        results.map(_.toOption) shouldBe Vector.fill(3)(Some(Vector("b", "a", "b")))
      }

      "preserves nested presence and present empty children without dropping sequence positions" in {
        Given("nested options, an empty model group and present empty collections")
        val supplied = Some(Some(Array[Any](Scalar("first"), AuditAttributes(), Vector.empty[Int], Json.Null)))
        When("the public boundary projects nested presence and top-level absence")
        val result = ModelValueEncoder.encodeC(supplied)
        val absent = ModelValueEncoder.encodeC(None)
        val nestedabsence = ModelValueEncoder.encodeC(Some(None))
        Then("Some layers, empty children and raw JSON null retain their distinct meanings")
        result.toOption shouldBe Some(Some(Some(Vector("first", Record.empty, Vector.empty, Json.Null))))
        absent.toOption shouldBe Some(None)
        nestedabsence.toOption shouldBe Some(Some(None))
      }

      "rejects absent and native-null model sequence positions without filtering" in {
        Given("ordered sequences with a condition projecting to absence, literal None or native null")
        val supplied = Vector[Any](Vector(Scalar("before"), Condition.Any, Scalar("after")),
          NonEmptyVector.one(Condition.Any), Array[Any](Scalar("before"), None), Vector[Any](Scalar("before"), null))
        When("the public boundary projects every sequence")
        val results = supplied.map(ModelValueEncoder.encodeC)
        Then("every invalid position fails the whole sequence including the single-element nonempty input")
        results.foreach(_ shouldBe a[Consequence.Failure[_]])
      }

      "retains raw Json identity and normalized Record ownership" in {
        Given("raw JSON null members and positions and a construction-normalized Record")
        val json = Json.obj("null" -> Json.Null, "positions" -> Json.arr(Json.Null, Json.fromInt(1)))
        val record = Record.data("first" -> 1, "first" -> 2, "raw" -> json)
        When("the public boundary projects Json and Record directly and through nominal wrappers")
        val jsonresults = Vector(json, Scalar(json)).map(ModelValueEncoder.encodeC)
        val recordresults = Vector(record, Scalar(record)).map(ModelValueEncoder.encodeC)
        Then("raw Json and Record instances survive with duplicate Record fields unchanged")
        jsonresults.foreach(result => result.toOption.get.asInstanceOf[Json] should be theSameInstanceAs json)
        recordresults.foreach(result => result.toOption.get.asInstanceOf[Record] should be theSameInstanceAs record)
        _pairs(record) shouldBe Vector("first" -> 1, "first" -> 2, "raw" -> json)
      }

      "projects selected Java temporal and URL values explicitly" in {
        Given("known Java values selected by the existing model reader contracts")
        val supplied = Vector[Any](Instant.parse("2026-01-02T03:04:05Z"), LocalDate.parse("2026-01-02"),
          ZonedDateTime.parse("2026-01-02T03:04:05+09:00[Asia/Tokyo]"),
          LocalDateTime.parse("2026-01-02T03:04:05"), OffsetDateTime.parse("2026-01-02T03:04:05+09:00"),
          new URL("https://example.org/path?q=1#part"))
        When("the public boundary projects the known Java representations")
        val results = supplied.map(ModelValueEncoder.encodeC)
        Then("each value has its explicit ISO or external-form text")
        results.map(_.toOption) shouldBe Vector(Some("2026-01-02T03:04:05Z"), Some("2026-01-02"),
          Some("2026-01-02T03:04:05+09:00[Asia/Tokyo]"), Some("2026-01-02T03:04:05"),
          Some("2026-01-02T03:04:05+09:00"), Some("https://example.org/path?q=1#part"))
      }

      "retains Core-owned aliases and explicit state and powertype presentation" in {
        Given("Core-owned attributes, a reference, standard Java context values and explicit model presentation")
        val supplied = Vector[Any](NameAttributes.simple("sample"), DescriptiveAttributes.empty,
          ContentAttributes(content = Some(ContentBody("body"))), ContentReferenceOccurrence(occurrenceIndex = 7),
          PostStatus.Archived, Power("selected"), StandardCharsets.UTF_8, Locale.JAPAN, TimeZone.getTimeZone("Asia/Tokyo"))
        When("the public boundary delegates those contracts to Core")
        val results = supplied.map(ModelValueEncoder.encodeC)
        Then("aliases retain their declared records and explicit print values stay scalar")
        results.map(_.toOption) shouldBe Vector(Some(Record.data("name" -> "sample")), Some(Record.empty),
          Some(Record.data("content" -> "body", "references" -> Vector.empty)), Some(Record.data("occurrence_index" -> 7)),
          Some("archived"), Some("selected"), Some("UTF-8"), Some("ja-JP"), Some("Asia/Tokyo"))
      }

      "retains String Map associations while recursively projecting model children" in {
        Given("String-keyed associations containing duplicate nominal values and absence")
        val supplied = Map[String, Any]("items" -> Array(Scalar("b"), Scalar("b")), "absent" -> Condition.Any)
        When("the public boundary delegates Map traversal")
        val result = ModelValueEncoder.encodeC(supplied)
        Then("the result remains an association rather than a Record and retains absent map values")
        result.toOption shouldBe Some(Map("items" -> Vector("b", "b"), "absent" -> None))
        result.toOption.get shouldBe a[Map[?, ?]]
      }

      "rejects unknown objects and unsupported Map keys without text callbacks" in {
        Given("unknown payload and key objects with observable forbidden text callbacks")
        val unknown = new UnknownValue
        val key = new UnknownValue
        val supplied = Vector[Any](unknown, Scalar(unknown), Map[Any, Any](key -> Scalar("value")))
        When("the public boundary projects the unsupported values")
        val results = supplied.map(ModelValueEncoder.encodeC)
        Then("each result is a failure and neither arbitrary toString callback was invoked")
        results.foreach(_ shouldBe a[Consequence.Failure[_]])
        unknown.calls shouldBe 0
        key.calls shouldBe 0
      }

      "rejects unsupported predicate conditions without evaluating them" in {
        Given("a predicate condition whose callback records any evaluation")
        var calls = 0
        val supplied = Condition.Predicate[String] { value => calls += 1; value.nonEmpty }
        When("the public boundary attempts to project the unsupported condition")
        val result = ModelValueEncoder.encodeC(supplied)
        Then("projection fails without invoking predicate semantics")
        result shouldBe a[Consequence.Failure[_]]
        calls shouldBe 0
      }
    }

    "satisfy connected projection laws" which {
      "preserves varying content, media identity, duplicates, raw Json and exact nominal numbers" in {
        Given("a total generator of model bodies, media counts and exact decimal payloads")
        val generator = for {
          text <- Gen.alphaStr
          count <- Gen.choose(1, 6)
          number <- Gen.choose(-100000, 100000)
          scale <- Gen.choose(1, 60)
        } yield (text, count, number, scale)
        val law = Prop.forAll(generator) { case (text, count, number, scale) =>
          val body = "body:" + text
          val content = ModelHygieneFixtures.content("owner", body)
          val ids = Vector.tabulate(count)(index => "asset" + index)
          val repeated = ids ++ ids.take(1)
          val media = MediaAttributes(None, repeated.map(id => Image(ObjectId(Identifier(id)), content)),
            Vector.empty, Vector.empty, Vector.empty)
          val expectedcontent = _expected_content("owner", body)
          val expectedimages = repeated.map(id => Record.data("id" -> id, "simpleobject" -> expectedcontent))
          val decimal = new BigDecimal(new java.math.BigDecimal(java.math.BigInteger.valueOf(number.toLong), scale), MathContext.UNLIMITED)
          val json = Json.arr(Json.Null, Json.fromString(text))
          val values = Array[Any](Scalar(decimal), json, Scalar(decimal))
          ModelValueEncoder.encodeC(content).toOption.contains(expectedcontent) &&
            ModelValueEncoder.encodeC(media).toOption.contains(Record.data("images" -> expectedimages,
              "audios" -> Vector.empty, "videos" -> Vector.empty, "attachments" -> Vector.empty)) &&
            ModelValueEncoder.encodeC(values).toOption.contains(Vector(decimal, json, decimal)) &&
            ModelValueEncoder.encodeC(NonEmptyVector.of(Scalar(decimal), Scalar(decimal))).toOption.contains(Vector(decimal, decimal))
        }
        When("ScalaCheck interprets at least one hundred successful connected model samples")
        val result = Test.check(Test.Parameters.default.withMinSuccessfulTests(100), law)
        Then("all independent expected model projections hold with zero discarded samples")
        result.passed shouldBe true
        result.succeeded should be >= 100
        result.discarded shouldBe 0
      }
    }
  }

  private def _pairs(value: Any): Vector[(String, Any)] =
    value.asInstanceOf[Record].fields.map(field => field.key -> field.value.single)

  private def _permissions(read: Boolean, write: Boolean, execute: Boolean): Record =
    Record.data("read" -> read, "write" -> write, "execute" -> execute)

  private def _expected_content(principal: String, body: String): Record =
    Record.data(
      "nameAttributes" -> Record.data("name" -> "sample"),
      "descriptiveAttributes" -> Record.empty,
      "contentAttributes" -> Record.data("content" -> body, "references" -> Vector.empty),
      "lifecycleAttributes" -> Record.data("createdAt" -> "1970-01-01T00:00:00Z", "updatedAt" -> "1970-01-01T00:00:00Z",
        "createdBy" -> "system", "updatedBy" -> "system", "postStatus" -> "draft", "aliveness" -> "alive"),
      "publicationAttributes" -> Record.empty,
      "securityAttributes" -> Record.data("ownerId" -> principal, "groupId" -> principal,
        "rights" -> Record.data("owner" -> _permissions(true, true, false),
          "group" -> _permissions(false, false, false), "other" -> _permissions(false, false, false)), "privilegeId" -> principal),
      "resourceAttributes" -> Record.data("activationStatus" -> "inactive"),
      "auditAttributes" -> Record.empty,
      "mediaAttributes" -> Record.data("images" -> Vector.empty, "audios" -> Vector.empty,
        "videos" -> Vector.empty, "attachments" -> Vector.empty),
      "contextualAttribute" -> Record.empty)
}

object ModelValueEncoderSpec {
  final case class Scalar(value: Any) extends NominalScalar with RecordPresentable {
    def toRecord(): Record = Record.data("presentation" -> "must not win")
  }

  final case class Power(value: String) extends Powertype

  final case class RecordPower(value: String) extends Powertype with RecordPresentable {
    override def dbValue: Option[Int] = Some(77)
    def toRecord(): Record = throw new IllegalStateException("forbidden powertype Record presentation")
  }

  final case class RecordState(override val value: String) extends StateMachine with RecordPresentable {
    def dbValue: Int = 77
    def toRecord(): Record = throw new IllegalStateException("forbidden state Record presentation")
  }

  final case class ThrowingPrintPower() extends Powertype with RecordPresentable {
    def value: String = "selected"
    override def print: String = throw new IllegalStateException("unavailable powertype print")
    def toRecord(): Record = throw new IllegalStateException("forbidden throwing-print Record presentation")
  }

  final class UnknownValue {
    private var _calls = 0
    def calls: Int = _calls
    override def toString: String = {
      _calls += 1
      throw new IllegalStateException("forbidden private payload text")
    }
  }
}
