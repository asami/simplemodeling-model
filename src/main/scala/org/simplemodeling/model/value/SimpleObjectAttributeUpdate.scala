package org.simplemodeling.model.value

import java.net.URL
import java.time.Instant
import java.time.ZonedDateTime
import cats.data.NonEmptyVector
import org.goldenport.datatype.Identifier
import org.goldenport.datatype.I18nBrief
import org.goldenport.datatype.I18nDescription
import org.goldenport.datatype.I18nLabel
import org.goldenport.datatype.I18nSummary
import org.goldenport.datatype.I18nText
import org.goldenport.datatype.I18nTitle
import org.goldenport.datatype.Name
import org.goldenport.datatype.ObjectId
import org.goldenport.datatype.Slug
import org.goldenport.Consequence
import org.goldenport.convert.ValueReader
import org.goldenport.record.Record
import org.goldenport.schema.XString
import org.simplemodeling.model.statemachine.Aliveness
import org.simplemodeling.model.statemachine.ActivationStatus
import org.simplemodeling.model.statemachine.PostStatus
import org.simplemodeling.model.directive.Update

/*
 * @since   Mar. 23, 2026
 * @version May.  3, 2026
 * @author  ASAMI, Tomoharu
 */
// NOTE:
// Temporary model to complete the current style first with execution
// prioritized.
// In a later phase, the structure will be reorganized toward better
// alignment with SimpleObject through ValueObject composition.
case class NameAttributesUpdate(
  name: Update[Name] = Update.noop[Name],
  label: Update[I18nLabel] = Update.noop[I18nLabel],
  title: Update[I18nTitle] = Update.noop[I18nTitle],
  code: Update[Identifier] = Update.noop[Identifier],
  alias: Update[NonEmptyVector[I18nLabel]] = Update.noop[NonEmptyVector[I18nLabel]],
  slug: Update[Slug] = Update.noop[Slug]
)

case class DescriptiveAttributesUpdate(
  headline: Update[I18nBrief] = Update.noop[I18nBrief],
  brief: Update[I18nBrief] = Update.noop[I18nBrief],
  summary: Update[I18nSummary] = Update.noop[I18nSummary],
  description: Update[I18nDescription] = Update.noop[I18nDescription],
  lead: Update[I18nSummary] = Update.noop[I18nSummary],
  `abstract`: Update[I18nSummary] = Update.noop[I18nSummary],
  remarks: Update[I18nSummary] = Update.noop[I18nSummary],
  tooltip: Update[I18nLabel] = Update.noop[I18nLabel]
)

case class ContentAttributesUpdate(
  content: Update[I18nText] = Update.noop[I18nText],
  mimeType: Update[String] = Update.noop[String],
  markup: Update[String] = Update.noop[String],
  references: Update[Vector[ContentReferenceOccurrence]] = Update.noop[Vector[ContentReferenceOccurrence]]
)

given ValueReader[ContentAttributesUpdate] =
  new ValueReader[ContentAttributesUpdate] {
    def readC(v: Any): Consequence[ContentAttributesUpdate] = v match {
      case m: ContentAttributesUpdate => Consequence.success(m)
      case m: ContentAttributes =>
        Consequence.success(
          ContentAttributesUpdate(
            content = m.content.map(Update.set).getOrElse(Update.noop),
            mimeType = m.mimeType.map(Update.set).getOrElse(Update.noop),
            markup = m.markup.map(Update.set).getOrElse(Update.noop),
            references = Update.set(m.references)
          )
        )
      case m: Record =>
        ContentAttributes.createC(m).map { attrs =>
          ContentAttributesUpdate(
            content = attrs.content.map(Update.set).getOrElse(Update.noop),
            mimeType = attrs.mimeType.map(Update.set).getOrElse(Update.noop),
            markup = attrs.markup.map(Update.set).getOrElse(Update.noop),
            references =
              if (m.getAny("references").orElse(m.getAny("contentReferences")).orElse(m.getAny("content_references")).isDefined)
                Update.set(attrs.references)
              else
                Update.noop
          )
        }
      case s: String => Consequence.success(ContentAttributesUpdate(content = Update.set(I18nText(s))))
      case _ => Consequence.failValueInvalid(v, XString)
    }
  }

case class LifecycleAttributesUpdate(
  createdAt: Update[Instant] = Update.noop[Instant],
  updatedAt: Update[Instant] = Update.noop[Instant],
  createdBy: Update[Identifier] = Update.noop[Identifier],
  updatedBy: Update[Identifier] = Update.noop[Identifier],
  postStatus: Update[PostStatus] = Update.noop[PostStatus],
  aliveness: Update[Aliveness] = Update.noop[Aliveness]
)

case class PublicationAttributesUpdate(
  publishAt: Update[ZonedDateTime] = Update.noop[ZonedDateTime],
  publicAt: Update[ZonedDateTime] = Update.noop[ZonedDateTime],
  closeAt: Update[ZonedDateTime] = Update.noop[ZonedDateTime],
  startAt: Update[ZonedDateTime] = Update.noop[ZonedDateTime],
  endAt: Update[ZonedDateTime] = Update.noop[ZonedDateTime]
)

case class SecurityAttributesUpdate(
  ownerId: Update[ObjectId] = Update.noop[ObjectId],
  groupId: Update[ObjectId] = Update.noop[ObjectId],
  rights: Update[SecurityAttributes.Rights] = Update.noop[SecurityAttributes.Rights],
  privilegeId: Update[ObjectId] = Update.noop[ObjectId]
)

case class ResourceAttributesUpdate(
  activatedAt: Update[ZonedDateTime] = Update.noop[ZonedDateTime],
  deactivatedAt: Update[ZonedDateTime] = Update.noop[ZonedDateTime],
  expiresAt: Update[ZonedDateTime] = Update.noop[ZonedDateTime],
  activationStatus: Update[ActivationStatus] = Update.noop[ActivationStatus]
)

case class AuditAttributesUpdate(
)

case class MediaAttributesUpdate(
  url: Update[URL] = Update.noop[URL],
  images: Update[Vector[Image]] = Update.noop[Vector[Image]],
  audios: Update[Vector[Audio]] = Update.noop[Vector[Audio]],
  videos: Update[Vector[Video]] = Update.noop[Vector[Video]],
  atathments: Update[Vector[Attachment]] = Update.noop[Vector[Attachment]]
)

case class ContextualAttributesUpdate(
)
