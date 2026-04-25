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
import org.simplemodeling.model.statemachine.Aliveness
import org.simplemodeling.model.statemachine.ActivationStatus
import org.simplemodeling.model.statemachine.PostStatus
import org.simplemodeling.model.directive.Condition

/*
 * @since   Mar. 23, 2026
 * @version Apr. 25, 2026
 * @author  ASAMI, Tomoharu
 */
// NOTE:
// This is completed first in the current style to establish working behavior.
// A future phase will move it toward the same ValueObject-composition
// direction as SimpleObject.
case class NameAttributesQuery(
  name: Condition[Name] = Condition.any[Name],
  label: Condition[I18nLabel] = Condition.any[I18nLabel],
  title: Condition[I18nTitle] = Condition.any[I18nTitle],
  code: Condition[Identifier] = Condition.any[Identifier],
  alias: Condition[NonEmptyVector[I18nLabel]] = Condition.any[NonEmptyVector[I18nLabel]],
  slug: Condition[Slug] = Condition.any[Slug]
)

case class DescriptiveAttributesQuery(
  headline: Condition[I18nBrief] = Condition.any[I18nBrief],
  brief: Condition[I18nBrief] = Condition.any[I18nBrief],
  summary: Condition[I18nSummary] = Condition.any[I18nSummary],
  description: Condition[I18nDescription] = Condition.any[I18nDescription],
  lead: Condition[I18nSummary] = Condition.any[I18nSummary],
  content: Condition[I18nText] = Condition.any[I18nText],
  `abstract`: Condition[I18nSummary] = Condition.any[I18nSummary],
  remarks: Condition[I18nSummary] = Condition.any[I18nSummary],
  tooltip: Condition[I18nLabel] = Condition.any[I18nLabel]
)

case class LifecycleAttributesQuery(
  createdAt: Condition[Instant] = Condition.any[Instant],
  updatedAt: Condition[Instant] = Condition.any[Instant],
  createdBy: Condition[Identifier] = Condition.any[Identifier],
  updatedBy: Condition[Identifier] = Condition.any[Identifier],
  postStatus: Condition[PostStatus] = Condition.any[PostStatus],
  aliveness: Condition[Aliveness] = Condition.any[Aliveness]
)

case class PublicationAttributesQuery(
  publishAt: Condition[ZonedDateTime] = Condition.any[ZonedDateTime],
  publicAt: Condition[ZonedDateTime] = Condition.any[ZonedDateTime],
  closeAt: Condition[ZonedDateTime] = Condition.any[ZonedDateTime],
  startAt: Condition[ZonedDateTime] = Condition.any[ZonedDateTime],
  endAt: Condition[ZonedDateTime] = Condition.any[ZonedDateTime]
)

case class SecurityAttributesQuery(
  ownerId: Condition[ObjectId] = Condition.any[ObjectId],
  groupId: Condition[ObjectId] = Condition.any[ObjectId],
  rights: Condition[SecurityAttributes.Rights] = Condition.any[SecurityAttributes.Rights],
  privilegeId: Condition[ObjectId] = Condition.any[ObjectId]
)

case class ResourceAttributesQuery(
  activatedAt: Condition[ZonedDateTime] = Condition.any[ZonedDateTime],
  deactivatedAt: Condition[ZonedDateTime] = Condition.any[ZonedDateTime],
  expiresAt: Condition[ZonedDateTime] = Condition.any[ZonedDateTime],
  activationStatus: Condition[ActivationStatus] = Condition.any[ActivationStatus]
)

case class AuditAttributesQuery(
)

case class MediaAttributesQuery(
  url: Condition[URL] = Condition.any[URL],
  images: Condition[Vector[Image]] = Condition.any[Vector[Image]],
  audios: Condition[Vector[Audio]] = Condition.any[Vector[Audio]],
  videos: Condition[Vector[Video]] = Condition.any[Vector[Video]],
  atathments: Condition[Vector[Attachment]] = Condition.any[Vector[Attachment]]
)

case class ContextualAttributesQuery(
)
