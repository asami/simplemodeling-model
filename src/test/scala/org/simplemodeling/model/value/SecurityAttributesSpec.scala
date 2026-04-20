package org.simplemodeling.model.value

import org.goldenport.record.Record
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

/*
 * @since   Dec. 22, 2025
 *  version Mar. 29, 2026
 * @version Apr. 20, 2026
 * @author  ASAMI, Tomoharu
 */
class SecurityAttributesSpec extends AnyWordSpec
  with ScalaCheckDrivenPropertyChecks
  with Matchers {

  "SecurityAttributes" should {
    "read expanded securityAttributes rights" in {
      val record = Record.dataAuto(
        "securityAttributes" -> SecurityAttributes.publicOwnedBy("alice").toRecord
      )

      val attributes = SecurityAttributes.fromRecord(record).get

      attributes.ownerId.id.value shouldBe "alice"
      attributes.permissionFor("owner", "update") shouldBe true
      attributes.permissionFor("other", "read") shouldBe true
      attributes.permissionFor("other", "update") shouldBe false
      attributes.permissionFor("owner", "execute") shouldBe false
      attributes.permissionFor("other", "execute") shouldBe false
    }

    "create private owner permissions" in {
      val attributes = SecurityAttributes.privateOwnedBy("alice")

      attributes.permissionFor("owner", "read") shouldBe true
      attributes.permissionFor("owner", "write") shouldBe true
      attributes.permissionFor("owner", "execute") shouldBe false
      attributes.permissionFor("group", "read") shouldBe false
      attributes.permissionFor("other", "read") shouldBe false
    }

    "read compact permission text" in {
      val record = Record.dataAuto(
        "owner_id" -> "alice",
        "permission" -> "owner=rwx,group=r,other=r"
      )

      val attributes = SecurityAttributes.fromRecord(record).get

      attributes.ownerId.id.value shouldBe "alice"
      attributes.permissionFor("owner", "delete") shouldBe true
      attributes.permissionFor("group", "read") shouldBe true
      attributes.permissionFor("group", "update") shouldBe false
    }

    "read JSON rights text from storage records" in {
      val record = Record.dataAuto(
        "owner_id" -> "alice",
        "rights" -> "{\"owner\":{\"read\":true,\"write\":true,\"execute\":true},\"group\":{\"read\":true,\"write\":false,\"execute\":false},\"other\":{\"read\":true,\"write\":false,\"execute\":false}}"
      )

      val attributes = SecurityAttributes.fromRecord(record).get

      attributes.ownerId.id.value shouldBe "alice"
      attributes.permissionFor("owner", "execute") shouldBe true
      attributes.permissionFor("group", "read") shouldBe true
      attributes.permissionFor("group", "update") shouldBe false
      attributes.permissionFor("other", "read") shouldBe true
    }
  }
}
