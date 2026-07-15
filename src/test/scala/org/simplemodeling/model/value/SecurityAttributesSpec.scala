package org.simplemodeling.model.value

import org.goldenport.record.Record
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

/*
 * @since   Dec. 22, 2025
 *  version Mar. 29, 2026
 *  version Apr. 20, 2026
 * @version Jul. 15, 2026
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

    "match a numeric subject id after ObjectId adds its generated prefix" in {
      val attributes = SecurityAttributes.privateOwnedBy("7hjjxQeJzVkVImNOpTs8o")

      attributes.ownerId.id.value shouldBe "id_7hjjxQeJzVkVImNOpTs8o"
      SecurityAttributes.roleFor(attributes, "7hjjxQeJzVkVImNOpTs8o", _ => false) shouldBe Some("owner")
      SecurityAttributes.roleFor(attributes, "id_7hjjxQeJzVkVImNOpTs8o", _ => false) shouldBe Some("owner")
    }

    "preserve an explicit alphabetic id prefix during owner matching" in {
      val attributes = SecurityAttributes.privateOwnedBy("id_alice")

      SecurityAttributes.roleFor(attributes, "id_alice", _ => false) shouldBe Some("owner")
      SecurityAttributes.roleFor(attributes, "alice", _ => false) shouldBe Some("other")
    }

    "preserve a raw id prefix followed directly by a digit" in {
      val attributes = SecurityAttributes.privateOwnedBy("id7alice")

      SecurityAttributes.roleFor(attributes, "id7alice", _ => false) shouldBe Some("owner")
      SecurityAttributes.roleFor(attributes, "7alice", _ => false) shouldBe Some("other")
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
