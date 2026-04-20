package org.simplemodeling.model.value

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import org.goldenport.convert.ValueReader
import org.goldenport.record.Record

/*
 * @since   Dec. 22, 2025
 *  version Mar. 29, 2026
 * @version Apr. 20, 2026
 * @author  ASAMI, Tomoharu
 */
class LifecycleAttributesSpec extends AnyWordSpec
  with ScalaCheckDrivenPropertyChecks
  with Matchers {

  "LifecycleAttributes" should {  "satisfy basic properties" in {
    pending
  }

  "preserve invariants" in {
    pending
  }

  "read storage principal identifiers with external separators" in {
    val record = Record.dataAuto(
      "created_by" -> "system",
      "updated_by" -> "test-app-content-manager-principal"
    )

    val result = summon[ValueReader[LifecycleAttributes]].readC(record).TAKE

    result.createdBy.value shouldBe "system"
    result.updatedBy.map(_.value) shouldBe Some("test_app_content_manager_principal")
  }
  }
}
