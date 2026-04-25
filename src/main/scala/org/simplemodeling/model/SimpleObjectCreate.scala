package org.simplemodeling.model

import java.time.Instant
import org.goldenport.datatype.Identifier
import org.goldenport.datatype.I18nText
import org.goldenport.datatype.Name
import org.goldenport.datatype.ObjectId
import org.simplemodeling.model.value.AuditAttributes
import org.simplemodeling.model.value.ContextualAttributes
import org.simplemodeling.model.value.DescriptiveAttributes
import org.simplemodeling.model.value.LifecycleAttributes
import org.simplemodeling.model.value.MediaAttributes
import org.simplemodeling.model.value.NameAttributes
import org.simplemodeling.model.value.PublicationAttributes
import org.simplemodeling.model.value.ResourceAttributes
import org.simplemodeling.model.value.SecurityAttributes

/*
 * @since   Mar. 23, 2026
 *  version Mar. 29, 2026
 * @version Apr. 25, 2026
 * @author  ASAMI, Tomoharu
 */
abstract class SimpleObjectCreate {
  // NOTE:
  // Current priority is executable behavior.
  // This create model is kept in the current shape first, and will be
  // refactored toward fuller ValueObject composition in a later phase.
  def nameAttributes: NameAttributes =
    NameAttributes.simple(Name("unknown"))

  def title: String =
    nameAttributes.title.map(_.displayMessage(java.util.Locale.ROOT)).getOrElse(nameAttributes.name.toString)

  def title(locale: java.util.Locale): String =
    nameAttributes.title.map(_.displayMessage(locale)).getOrElse(nameAttributes.name.toString)

  def descriptiveAttributes: DescriptiveAttributes =
    DescriptiveAttributes.empty

  def content: Option[I18nText] =
    descriptiveAttributes.content

  def content(locale: java.util.Locale): Option[String] =
    content.map(_.displayMessage(locale))

  def lifecycleAttributes: LifecycleAttributes =
    LifecycleAttributes(
      createdAt = Instant.EPOCH,
      updatedAt = Instant.EPOCH,
      createdBy = Identifier("system"),
      updatedBy = Identifier("system"),
      postStatus = org.simplemodeling.model.statemachine.PostStatus.default,
      aliveness = org.simplemodeling.model.statemachine.Aliveness.default
    )

  def publicationAttributes: PublicationAttributes =
    PublicationAttributes(
      publishAt = None,
      publicAt = None,
      closeAt = None,
      startAt = None,
      endAt = None
    )

  def securityAttributes: SecurityAttributes = {
    val sid = ObjectId(Identifier("system"))
    SecurityAttributes(
      ownerId = sid,
      groupId = sid,
      rights = SecurityAttributes.Rights(
        owner = SecurityAttributes.Rights.Permissions(read = true, write = true, execute = true),
        group = SecurityAttributes.Rights.Permissions(read = true, write = false, execute = false),
        other = SecurityAttributes.Rights.Permissions(read = true, write = false, execute = false)
      ),
      privilegeId = sid
    )
  }

  def resourceAttributes: ResourceAttributes =
    ResourceAttributes()

  def auditAttributes: AuditAttributes =
    AuditAttributes()

  def mediaAttributes: MediaAttributes =
    MediaAttributes(
      url = None,
      images = Vector.empty,
      audios = Vector.empty,
      videos = Vector.empty,
      atathments = Vector.empty
    )

  def contextualAttribute: ContextualAttributes =
    ContextualAttributes()
}
