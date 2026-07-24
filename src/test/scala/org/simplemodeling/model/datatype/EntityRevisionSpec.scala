package org.simplemodeling.model.datatype

import io.circe.parser.decode
import io.circe.syntax.*
import org.goldenport.{Conclusion, Consequence}
import org.goldenport.convert.ValueReader
import org.goldenport.observation.{Cause, Descriptor, Taxonomy}
import org.goldenport.schema.XPositiveInteger
import org.scalacheck.Gen
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

/*
 * @since   Jul. 25, 2026
 * @version Jul. 25, 2026
 * @author  ASAMI, Tomoharu
 */
final class EntityRevisionSpec extends AnyWordSpec
    with GivenWhenThen
    with Matchers
    with ScalaCheckDrivenPropertyChecks {
  private val _valid_revision = Gen.chooseNum(EntityRevision.MINIMUM, EntityRevision.MAXIMUM)
  private val _invalid_non_positive_revision = Gen.chooseNum(Long.MinValue, 0L)
  private val _expected_revision = s"an integer from ${EntityRevision.MINIMUM} through ${EntityRevision.MAXIMUM}"

  "EntityRevision" should {
    "use one as the canonical initial revision and positive-integer schema" in {
      Given("the SimpleEntity revision datatype")

      When("the framework requests its initial value and schema datatype")
      val result = EntityRevision.INITIAL
      val datatype = EntityRevision.DATA_TYPE

      Then("the initial revision is one and its schema is positive integer")
      result.value shouldBe 1L
      datatype shouldBe XPositiveInteger
    }

    "admit every positive Long value" in {
      forAll(_valid_revision) { value =>
        Given("a revision value in the supported positive Long range")

        When("the value is validated")
        val result = EntityRevision.createC(value)

        Then("the validated revision preserves the value")
        result.toOption.map(_.value) shouldBe Some(value)
      }
    }

    "reject every non-positive Long value with structured field diagnostics" in {
      forAll(_invalid_non_positive_revision) { value =>
        Given("a revision value outside the positive range")

        When("the value is validated")
        val conclusion = _failure(EntityRevision.createC(value))
        val facets = conclusion.observation.cause.descriptor.facets

        Then("validation identifies the invalid revision field and values")
        conclusion.observation.taxonomy shouldBe Taxonomy.argumentInvalid
        facets should contain (Descriptor.Facet.FieldPath("revision"))
        facets should contain (Descriptor.Facet.Expected(_expected_revision))
        facets should contain (Descriptor.Facet.Actual(value))
      }
    }

    "reuse core Long decoding before applying revision validation" in {
      val reader = summon[ValueReader[EntityRevision]]
      val accepted = Vector[Any](1, 2L, "3")
      val expected = Vector(1L, 2L, 3L)

      accepted.zip(expected).foreach { case (input, value) =>
        Given(s"revision input $input accepted by the core Long reader")

        When("the EntityRevision ValueReader decodes it")
        val result = reader.readC(input)

        Then("the decoded revision retains the core Long value")
        result.toOption.map(_.value) shouldBe Some(value)
      }
    }

    "preserve core Long rejection for fractional, out-of-range, empty, and nonnumeric values" in {
      val reader = summon[ValueReader[EntityRevision]]
      val invalid = Vector[Any](1.5d, BigDecimal("1.1"), BigInt(Long.MaxValue) + 1, "", "revision", null)

      invalid.foreach { input =>
        Given(s"input rejected by the core Long reader: ${Option(input).fold("null")(_.toString)}")

        When("the EntityRevision ValueReader decodes it")
        val conclusion = _failure(reader.readC(input))

        Then("the core invalid-value taxonomy is preserved")
        conclusion.observation.taxonomy shouldBe Taxonomy.valueInvalid
      }
    }

    "serialize as a validated scalar" in {
      Given("a validated Entity revision")
      val revision = EntityRevision.createC(42L).toOption.get

      When("the revision is encoded and decoded as JSON")
      val encoded = revision.asJson.noSpaces
      val decoded = decode[EntityRevision](encoded)
      val invalid = decode[EntityRevision]("0")

      Then("the wire representation is scalar and remains validated")
      encoded shouldBe "42"
      decoded shouldBe Right(revision)
      invalid.isLeft shouldBe true
    }

    "advance without overflow and describe upper-bound exhaustion structurally" in {
      Given("an ordinary revision and the maximum revision")
      val ordinary = EntityRevision.createC(41L).toOption.get
      val maximum = EntityRevision.createC(Long.MaxValue).toOption.get

      When("both revisions are advanced")
      val advanced = ordinary.nextC
      val conclusion = _failure(maximum.nextC)
      val cause = conclusion.observation.cause
      val facets = cause.descriptor.facets

      Then("the ordinary revision advances exactly once")
      advanced.toOption.map(_.value) shouldBe Some(42L)

      And("maximum advancement reports its limit and policy without wrapping")
      cause.kind shouldBe Some(Cause.Kind.Limit)
      facets should contain (Descriptor.Facet.FieldPath("revision"))
      facets should contain (Descriptor.Facet.Policy("entity.revision.advance"))
      facets should contain (Descriptor.Facet.Limit(EntityRevision.MAXIMUM))
      facets should contain (Descriptor.Facet.Actual(BigInt(EntityRevision.MAXIMUM) + 1))
      maximum.value shouldBe Long.MaxValue
    }
  }

  private def _failure[A](result: Consequence[A]): Conclusion =
    result match
      case Consequence.Failure(conclusion) => conclusion
      case _ => fail("Expected Consequence.Failure")
}
