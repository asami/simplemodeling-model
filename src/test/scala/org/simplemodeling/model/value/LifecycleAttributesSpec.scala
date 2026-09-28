package org.simplemodeling.model.value

import java.time.Instant
import org.scalacheck.Gen
import org.scalatest.GivenWhenThen
import java.time.ZonedDateTime
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import org.goldenport.Consequence
import org.goldenport.convert.ValueReader
import org.goldenport.record.Record

/*
 * @since   Dec. 22, 2025
 *  version Mar. 29, 2026
 *  version Apr. 25, 2026
 * @version Sep. 28, 2026
 * @author  ASAMI, Tomoharu
 */
class LifecycleAttributesSpec extends AnyWordSpec
  with ScalaCheckDrivenPropertyChecks
  with Matchers
  with GivenWhenThen {

  "LifecycleAttributes" should {
  "retain the supplied creation and update metadata" in {
    Given("explicit creation and update timestamps and principals")
    val at = Instant.parse("2026-04-25T00:00:00Z")

    When("the lifecycle value is constructed")
    val lifecycle = LifecycleAttributes(
      createdAt = at,
      updatedAt = at,
      createdBy = org.goldenport.datatype.Identifier("system"),
      updatedBy = org.goldenport.datatype.Identifier("system"),
      postStatus = org.simplemodeling.model.statemachine.PostStatus.default,
      aliveness = org.simplemodeling.model.statemachine.Aliveness.default
    )

    Then("both timestamp and principal pairs retain their supplied values")
    lifecycle.createdAt shouldBe at
    lifecycle.updatedAt shouldBe at
    lifecycle.createdBy.value shouldBe "system"
    lifecycle.updatedBy.value shouldBe "system"
  }

  "preserve creation metadata when updated storage fields are omitted" in {
    forAll(Gen.chooseNum(0L, 4102444800L), Gen.alphaStr) { (seconds, suffix) =>
      Given("a sparse legacy record with generated creation metadata")
      val timestamp = Instant.ofEpochSecond(seconds)
      val principal = "actor" + suffix
      val record = Record.dataAuto("created_at" -> timestamp.toString, "created_by" -> principal)
      When("the lifecycle storage reader interprets the sparse record")
      val result = summon[ValueReader[LifecycleAttributes]].readC(record).toOption.get
      Then("updated metadata inherits creation metadata without changing default state values")
      result.createdAt shouldBe timestamp
      result.updatedAt shouldBe timestamp
      result.createdBy.value shouldBe principal
      result.updatedBy shouldBe result.createdBy
      result.postStatus shouldBe org.simplemodeling.model.statemachine.PostStatus.default
      result.aliveness shouldBe org.simplemodeling.model.statemachine.Aliveness.default
    }
  }

  "read storage principal identifiers with external separators" in {
    Given("a storage principal using external separators")
    val at = "2026-04-25T00:00:00Z"
    val record = Record.dataAuto(
      "created_at" -> at,
      "updated_at" -> at,
      "created_by" -> "system",
      "updated_by" -> "test-app-content-manager-principal"
    )

    When("the lifecycle record is interpreted")
    val result = summon[ValueReader[LifecycleAttributes]].readC(record).TAKE

    Then("the stored creation and update metadata is retained with existing defaults")

    result.createdAt shouldBe Instant.parse(at)
    result.updatedAt shouldBe Instant.parse(at)
    result.createdBy.value shouldBe "system"
    result.updatedBy.value shouldBe "test_app_content_manager_principal"
  }

  "default updated fields to created fields for legacy sparse records" in {
    Given("a legacy record containing only creation fields")
    val at = "2026-04-25T00:00:00Z"
    val record = Record.dataAuto(
      "created_at" -> at,
      "created_by" -> "system"
    )

    When("the lifecycle record is interpreted")
    val result = summon[ValueReader[LifecycleAttributes]].readC(record).TAKE

    Then("the stored creation and update metadata is retained with existing defaults")

    result.createdAt shouldBe Instant.parse(at)
    result.updatedAt shouldBe Instant.parse(at)
    result.createdBy.value shouldBe "system"
    result.updatedBy.value shouldBe "system"
  }

  "preserve audit principals in summary projection" in {
    Given("distinct creation and update principals")
    val at = Instant.parse("2026-04-25T00:00:00Z")
    val lifecycle = LifecycleAttributes(
      createdAt = at,
      updatedAt = at,
      createdBy = org.goldenport.datatype.Identifier("alice"),
      updatedBy = org.goldenport.datatype.Identifier("bob"),
      postStatus = org.simplemodeling.model.statemachine.PostStatus.default,
      aliveness = org.simplemodeling.model.statemachine.Aliveness.default
    )

    When("the lifecycle is projected into its summary")
    val projected = org.simplemodeling.model.value.summary.LifecycleAttributes(lifecycle)

    Then("the summary retains both audit principals")
    projected.createdBy.value shouldBe "alice"
    projected.updatedBy.value shouldBe "bob"
  }

  "reject ZonedDateTime lifecycle values instead of normalizing them" in {
    Given("a ZonedDateTime where storage requires an Instant-compatible lifecycle field")
    val record = Record.dataAuto(
      "created_at" -> ZonedDateTime.parse("2026-04-25T00:00:00Z"),
      "updated_at" -> "2026-04-25T00:00:00Z"
    )

    When("the incompatible lifecycle value is interpreted")
    val result = summon[ValueReader[LifecycleAttributes]].readC(record)
    Then("the incompatible type is rejected without timezone normalization")
    result shouldBe a[Consequence.Failure[_]]
  }
  }
}
