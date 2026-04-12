package org.simplemodeling.model.value

import org.goldenport.datatype.ObjectId
import org.goldenport.datatype.Identifier
import org.goldenport.datatype.PathName
import org.goldenport.record.Record

/*
 * @since   Aug.  1, 2025
 *  version Aug.  2, 2025
 *  version Mar. 29, 2026
 * @version Apr. 13, 2026
 * @author  ASAMI, Tomoharu
 */
case class SecurityAttributes(
  ownerId: ObjectId,
  groupId: ObjectId,
  rights: SecurityAttributes.Rights,
  privilegeId: ObjectId
) {
  def permissionFor(role: String, accessKind: String): Boolean =
    SecurityAttributes.permission(this, role).allows(accessKind)

  def toRecord: Record =
    Record.dataAuto(
      "owner_id" -> ownerId.id.value,
      "group_id" -> groupId.id.value,
      "privilege_id" -> privilegeId.id.value,
      "rights" -> rights.toRecord
    )
}

object SecurityAttributes {
  case class Rights(
    owner: Rights.Permissions,
    group: Rights.Permissions,
    other: Rights.Permissions
  ) {
    def toRecord: Record =
      Record.dataAuto(
        "owner" -> owner.toRecord,
        "group" -> group.toRecord,
        "other" -> other.toRecord
      )
  }
  object Rights {
    case class Permissions(
      read: Boolean,
      write: Boolean,
      execute: Boolean
    ) {
      def allows(accessKind: String): Boolean =
        accessKind match
          case "read" | "search/list" => read
          case "update" => write
          case "delete" => execute || write
          case "create" => false
          case "write" => write
          case "execute" => execute
          case _ => false

      def toRecord: Record =
        Record.dataAuto(
          "read" -> read,
          "write" -> write,
          "execute" -> execute
        )
    }
    object Permissions {
      val none: Permissions = Permissions(read = false, write = false, execute = false)
      val readOnly: Permissions = Permissions(read = true, write = false, execute = false)
      val readWrite: Permissions = Permissions(read = true, write = true, execute = false)
      val full: Permissions = Permissions(read = true, write = true, execute = true)

      def fromRecord(record: Record): Permissions =
        Permissions(
          read = record.getBoolean("read").getOrElse(false),
          write = record.getBoolean("write").getOrElse(false),
          execute = record.getBoolean("execute").getOrElse(false)
        )

      def parse(text: String): Permissions = {
        val normalized = text.trim.toLowerCase(java.util.Locale.ROOT)
        Permissions(
          read = normalized.exists(_ == 'r') || normalized.contains("read"),
          write = normalized.exists(_ == 'w') || normalized.contains("write"),
          execute = normalized.exists(_ == 'x') || normalized.contains("execute")
        )
      }
    }
  }

  trait Holder {
    def securityAttributes: SecurityAttributes

    protected def security_Attributes: SecurityAttributes = securityAttributes
  }

  def ownedBy(
    principalId: String,
    owner: Rights.Permissions = Rights.Permissions.readWrite,
    group: Rights.Permissions = Rights.Permissions.none,
    other: Rights.Permissions = Rights.Permissions.none
  ): SecurityAttributes = {
    val principal = _object_id(principalId)
    SecurityAttributes(
      ownerId = principal,
      groupId = principal,
      rights = Rights(
        owner = owner,
        group = group,
        other = other
      ),
      privilegeId = principal
    )
  }

  def publicOwnedBy(principalId: String): SecurityAttributes =
    ownedBy(
      principalId,
      group = Rights.Permissions.readOnly,
      other = Rights.Permissions.readOnly
    )

  def privateOwnedBy(principalId: String): SecurityAttributes =
    ownedBy(principalId)

  def fromRecord(record: Record): Option[SecurityAttributes] = {
    val security = _security_record(record)
    val ownerid = _first_string(record, security, "ownerId", "owner_id", "createdBy", "created_by")
    val groupid = _first_string(record, security, "groupId", "group_id", "group")
    val privilegeid = _first_string(record, security, "privilegeId", "privilege_id", "privilege")
    val ownerperm = _permission(record, security, "owner")
    val groupperm = _permission(record, security, "group")
    val otherperm = _permission(record, security, "other")
    ownerid.map { owner =>
      val group0 = groupid.getOrElse(owner)
      val privilege0 = privilegeid.getOrElse(owner)
      SecurityAttributes(
        ownerId = _object_id(owner),
        groupId = _object_id(group0),
        rights = Rights(ownerperm, groupperm, otherperm),
        privilegeId = _object_id(privilege0)
      )
    }
  }

  def roleFor(
    attributes: SecurityAttributes,
    subjectId: String,
    matchesGroup: String => Boolean
  ): Option[String] =
    if (_normalize(attributes.ownerId.id.value) == _normalize(subjectId))
      Some("owner")
    else if (matchesGroup(attributes.groupId.id.value))
      Some("group")
    else
      Some("other")

  def permission(
    attributes: SecurityAttributes,
    role: String
  ): Rights.Permissions =
    _normalize(role) match
      case "owner" => attributes.rights.owner
      case "group" => attributes.rights.group
      case _ => attributes.rights.other

  private def _security_record(record: Record): Option[Record] =
    record.getRecord("securityAttributes")
      .orElse(record.getRecord("security_attributes"))

  private def _first_string(
    record: Record,
    security: Option[Record],
    names: String*
  ): Option[String] =
    names.toVector.iterator.flatMap { name =>
      val fromsecurity = security.flatMap(_.getString(name))
      val fromrecord = record.getString(name)
      val frompath = security match {
        case Some(_) => None
        case None =>
          record.getString(PathName(Vector("securityAttributes", name)))
            .orElse(record.getString(PathName(Vector("security_attributes", name))))
      }
      Vector(fromsecurity, fromrecord, frompath).flatten
    }.map(_.trim).find(_.nonEmpty)

  private def _permission(
    record: Record,
    security: Option[Record],
    role: String
  ): Rights.Permissions =
    _permission_record(security, role)
      .orElse(_permission_record(Some(record), role, Vector("securityAttributes", "rights")))
      .orElse(_permission_record(Some(record), role, Vector("security_attributes", "rights")))
      .map(Rights.Permissions.fromRecord)
      .orElse(_permission_text(record, role).map(Rights.Permissions.parse))
      .getOrElse(Rights.Permissions.none)

  private def _permission_record(
    record: Option[Record],
    role: String,
    prefix: Vector[String] = Vector("rights")
  ): Option[Record] =
    record.flatMap(r => _get_record(r, prefix :+ role))

  private def _get_record(
    record: Record,
    path: Vector[String]
  ): Option[Record] =
    path.toList match
      case Nil => Some(record)
      case key :: Nil => record.getRecord(key)
      case key :: rest => record.getRecord(key).flatMap(_get_record(_, rest.toVector))

  private def _permission_text(
    record: Record,
    role: String
  ): Option[String] =
    record.getString("permission")
      .flatMap { text =>
        text.split("[,;]").toVector.map(_.trim).find { item =>
          item.toLowerCase(java.util.Locale.ROOT).startsWith(s"$role=") ||
          item.toLowerCase(java.util.Locale.ROOT).startsWith(s"$role:")
        }
      }
      .flatMap(_.split("[=:]", 2).lift(1))

  private def _object_id(text: String): ObjectId =
    ObjectId(Identifier(_identifier_text(text)))

  private def _normalize(text: String): String =
    text.toLowerCase(java.util.Locale.ROOT).replace("_", "").replace("-", "")

  private def _identifier_text(text: String): String = {
    val sanitized = text.trim.map {
      case c if c.isLetterOrDigit || c == '_' => c
      case _ => '_'
    }.mkString
    val nonempty = if (sanitized.isEmpty) "unknown" else sanitized
    if (nonempty.headOption.exists(_.isLetter))
      nonempty
    else
      s"id_$nonempty"
  }
}
