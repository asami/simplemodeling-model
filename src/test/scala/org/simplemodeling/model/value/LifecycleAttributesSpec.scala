package org.simplemodeling.model.value

import java.time.Instant
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
 * @version Apr. 25, 2026
 * @author  ASAMI, Tomoharu
 */
class LifecycleAttributesSpec extends AnyWordSpec
  with ScalaCheckDrivenPropertyChecks
  with Matchers {

  "LifecycleAttributes" should {  "satisfy basic properties" in {
    val at = Instant.parse("2026-04-25T00:00:00Z")

    val lifecycle = LifecycleAttributes(
      createdAt = at,
      updatedAt = at,
      createdBy = org.goldenport.datatype.Identifier("system"),
      updatedBy = org.goldenport.datatype.Identifier("system"),
      postStatus = org.simplemodeling.model.statemachine.PostStatus.default,
      aliveness = org.simplemodeling.model.statemachine.Aliveness.default
    )

    lifecycle.createdAt shouldBe at
    lifecycle.updatedAt shouldBe at
    lifecycle.createdBy.value shouldBe "system"
    lifecycle.updatedBy.value shouldBe "system"
  }

  "preserve invariants" in {
    pending
  }

  "read storage principal identifiers with external separators" in {
    val at = "2026-04-25T00:00:00Z"
    val record = Record.dataAuto(
      "created_at" -> at,
      "updated_at" -> at,
      "created_by" -> "system",
      "updated_by" -> "test-app-content-manager-principal"
    )

    val result = summon[ValueReader[LifecycleAttributes]].readC(record).TAKE

    result.createdAt shouldBe Instant.parse(at)
    result.updatedAt shouldBe Instant.parse(at)
    result.createdBy.value shouldBe "system"
    result.updatedBy.value shouldBe "test_app_content_manager_principal"
  }

  "default updated fields to created fields for legacy sparse records" in {
    val at = "2026-04-25T00:00:00Z"
    val record = Record.dataAuto(
      "created_at" -> at,
      "created_by" -> "system"
    )

    val result = summon[ValueReader[LifecycleAttributes]].readC(record).TAKE

    result.createdAt shouldBe Instant.parse(at)
    result.updatedAt shouldBe Instant.parse(at)
    result.createdBy.value shouldBe "system"
    result.updatedBy.value shouldBe "system"
  }

  "preserve audit principals in summary projection" in {
    val at = Instant.parse("2026-04-25T00:00:00Z")
    val lifecycle = LifecycleAttributes(
      createdAt = at,
      updatedAt = at,
      createdBy = org.goldenport.datatype.Identifier("alice"),
      updatedBy = org.goldenport.datatype.Identifier("bob"),
      postStatus = org.simplemodeling.model.statemachine.PostStatus.default,
      aliveness = org.simplemodeling.model.statemachine.Aliveness.default
    )

    val projected = org.simplemodeling.model.value.summary.LifecycleAttributes(lifecycle)

    projected.createdBy.value shouldBe "alice"
    projected.updatedBy.value shouldBe "bob"
  }

  "reject ZonedDateTime lifecycle values instead of normalizing them" in {
    val record = Record.dataAuto(
      "created_at" -> ZonedDateTime.parse("2026-04-25T00:00:00Z"),
      "updated_at" -> "2026-04-25T00:00:00Z"
    )

    summon[ValueReader[LifecycleAttributes]].readC(record) shouldBe a[Consequence.Failure[_]]
  }
  }
}
