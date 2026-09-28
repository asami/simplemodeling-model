package org.simplemodeling.model

import java.time.Instant
import org.goldenport.datatype.Identifier
import org.simplemodeling.model.datatype.{EntityId, EntityRevision}
import org.simplemodeling.model.statemachine.{Aliveness, PostStatus}
import org.simplemodeling.model.value.*

/*
 * @since   Sep. 28, 2026
 * @version Sep. 28, 2026
 */
private[model] object ModelHygieneFixtures {
  def content(principal: String, body: String): SimpleObjectContent =
    SimpleObjectContent(
      nameAttributes = NameAttributes.simple("sample"),
      descriptiveAttributes = DescriptiveAttributes.empty,
      contentAttributes = ContentAttributes(content = Some(ContentBody(body))),
      lifecycleAttributes = LifecycleAttributes(
        Instant.EPOCH, Instant.EPOCH, Identifier("system"), Identifier("system"),
        PostStatus.default, Aliveness.default
      ),
      publicationAttributes = PublicationAttributes(None, None, None, None, None),
      securityAttributes = SecurityAttributes.ownedBy(principal),
      resourceAttributes = ResourceAttributes(),
      auditAttributes = AuditAttributes(),
      mediaAttributes = MediaAttributes(None, Vector.empty, Vector.empty, Vector.empty, Vector.empty),
      contextualAttribute = ContextualAttributes()
    )

  def objectView(attributes: SimpleObjectContent): SimpleObject =
    new SimpleObject with SimpleObjectContent.Holder {
      protected def simple_Object_Content: SimpleObjectContent = attributes
    }

  def entityView(attributes: SimpleObjectContent, identity: EntityId, persistenceRevision: EntityRevision): SimpleEntity =
    new SimpleEntity with SimpleObjectContent.Holder {
      val id: EntityId = identity
      val revision: EntityRevision = persistenceRevision
      protected def simple_Object_Content: SimpleObjectContent = attributes
    }
}
