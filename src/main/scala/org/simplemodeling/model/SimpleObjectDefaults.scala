package org.simplemodeling.model

import java.time.Instant
import org.goldenport.datatype.Identifier
import org.goldenport.datatype.Name
import org.goldenport.datatype.ObjectId
import org.simplemodeling.model.value.NameAttributes
import org.simplemodeling.model.value.DescriptiveAttributes
import org.simplemodeling.model.value.LifecycleAttributes
import org.simplemodeling.model.value.PublicationAttributes
import org.simplemodeling.model.value.SecurityAttributes
import org.simplemodeling.model.value.ResourceAttributes
import org.simplemodeling.model.value.AuditAttributes
import org.simplemodeling.model.value.MediaAttributes
import org.simplemodeling.model.value.ContextualAttributes
import org.simplemodeling.model.value.ContentAttributes

/*
 * @since   Mar. 23, 2026
 *  version Mar. 29, 2026
 * @version May.  3, 2026
 * @author  ASAMI, Tomoharu
 */
trait SimpleObjectDefaults extends SimpleObject {
  override def nameAttributes: NameAttributes =
    NameAttributes.simple(Name("unknown"))

  override def descriptiveAttributes: DescriptiveAttributes =
    DescriptiveAttributes.empty

  override def contentAttributes: ContentAttributes =
    ContentAttributes.empty

  override def lifecycleAttributes: LifecycleAttributes =
    LifecycleAttributes(
      createdAt = Instant.EPOCH,
      updatedAt = Instant.EPOCH,
      createdBy = Identifier("system"),
      updatedBy = Identifier("system"),
      postStatus = org.simplemodeling.model.statemachine.PostStatus.default,
      aliveness = org.simplemodeling.model.statemachine.Aliveness.default
    )

  override def publicationAttributes: PublicationAttributes =
    PublicationAttributes(
      publishAt = None,
      publicAt = None,
      closeAt = None,
      startAt = None,
      endAt = None
    )

  override def securityAttributes: SecurityAttributes = {
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

  override def resourceAttributes: ResourceAttributes =
    ResourceAttributes()

  override def auditAttributes: AuditAttributes =
    AuditAttributes()

  override def mediaAttributes: MediaAttributes =
    MediaAttributes(
      url = None,
      images = Vector.empty,
      audios = Vector.empty,
      videos = Vector.empty,
      atathments = Vector.empty
    )

  override def contextualAttribute: ContextualAttributes =
    ContextualAttributes()
}
