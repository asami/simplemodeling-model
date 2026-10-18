package org.simplemodeling.model.value

import java.net.URL
import java.time.{Instant, LocalDate, LocalDateTime, OffsetDateTime, ZonedDateTime}
import org.goldenport.Consequence
import org.goldenport.convert.ExternalValueEncoder
import org.goldenport.record.Record
import org.simplemodeling.model.directive.{Condition, Update}
import org.simplemodeling.model.powertype.Powertype
import org.simplemodeling.model.statemachine.StateMachine

/*
 * Model-owned projections for external Record construction.
 *
 * @since   Oct. 18, 2026
 * @version Oct. 18, 2026
 */
object ModelValueEncoder {
  def encodeC(value: Any): Consequence[Any] =
    ExternalValueEncoder.encodeC(value, _model_adapter)

  private val _model_adapter: ExternalValueEncoder.Adapter = {
    case value: NominalScalar => encodeC(value.value)
    case value: StateMachine => Consequence(value.print).flatMap(encodeC)
    case value: Powertype => Consequence(value.print).flatMap(encodeC)
    case Condition.Any => Consequence.success(None)
    case Condition.Is(expected) => encodeC(expected)
    case Condition.In(candidates) => encodeC(candidates.toVector)
    case Update.Noop => Consequence.success(Update.Noop)
    case Update.SetNull => Consequence.success(Update.SetNull)
    case Update.SetValue(value) => encodeC(value).map(Update.SetValue(_))
    case value: LifecycleAttributes => _record(
      "createdAt" -> value.createdAt,
      "updatedAt" -> value.updatedAt,
      "createdBy" -> value.createdBy,
      "updatedBy" -> value.updatedBy,
      "postStatus" -> value.postStatus,
      "aliveness" -> value.aliveness)
    case value: PublicationAttributes => _record(
      "publishAt" -> value.publishAt,
      "publicAt" -> value.publicAt,
      "closeAt" -> value.closeAt,
      "startAt" -> value.startAt,
      "endAt" -> value.endAt)
    case value: SecurityAttributes => _record(
      "ownerId" -> value.ownerId,
      "groupId" -> value.groupId,
      "rights" -> value.rights,
      "privilegeId" -> value.privilegeId)
    case value: SecurityAttributes.Rights => _record(
      "owner" -> value.owner,
      "group" -> value.group,
      "other" -> value.other)
    case value: SecurityAttributes.Rights.Permissions => _record(
      "read" -> value.read,
      "write" -> value.write,
      "execute" -> value.execute)
    case value: ResourceAttributes => _record(
      "activatedAt" -> value.activatedAt,
      "deactivatedAt" -> value.deactivatedAt,
      "expiresAt" -> value.expiresAt,
      "activationStatus" -> value.activationStatus)
    case _: AuditAttributes => Consequence.success(Record.empty)
    case _: ContextualAttributes => Consequence.success(Record.empty)
    case value: MediaAttributes => _record(
      "url" -> value.url,
      "images" -> value.images,
      "audios" -> value.audios,
      "videos" -> value.videos,
      "attachments" -> value.atathments)
    case value: SimpleObjectContent => _record(
      "nameAttributes" -> value.nameAttributes,
      "descriptiveAttributes" -> value.descriptiveAttributes,
      "contentAttributes" -> value.contentAttributes,
      "lifecycleAttributes" -> value.lifecycleAttributes,
      "publicationAttributes" -> value.publicationAttributes,
      "securityAttributes" -> value.securityAttributes,
      "resourceAttributes" -> value.resourceAttributes,
      "auditAttributes" -> value.auditAttributes,
      "mediaAttributes" -> value.mediaAttributes,
      "contextualAttribute" -> value.contextualAttribute)
    case value: Image => _record("id" -> value.id, "simpleobject" -> value.simpleobject)
    case value: Audio => _record("id" -> value.id, "simpleobject" -> value.simpleobject)
    case value: Video => _record("id" -> value.id, "simpleobject" -> value.simpleobject)
    case value: Attachment => _record("id" -> value.id, "simpleobject" -> value.simpleobject)
    case value: Instant => Consequence.success(value.toString)
    case value: LocalDate => Consequence.success(value.toString)
    case value: ZonedDateTime => Consequence.success(value.toString)
    case value: LocalDateTime => Consequence.success(value.toString)
    case value: OffsetDateTime => Consequence.success(value.toString)
    case value: URL => Consequence.success(value.toExternalForm)
  }

  private def _record(fields: (String, Any)*): Consequence[Any] =
    Consequence.zipN(fields.map { case (key, value) =>
      encodeC(value).map(key -> _)
    }).flatMap(fields => Consequence(Record.dataAuto(fields*)))
}
