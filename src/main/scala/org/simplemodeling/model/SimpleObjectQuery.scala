package org.simplemodeling.model

import org.simplemodeling.model.value.AuditAttributesQuery
import org.simplemodeling.model.value.ContextualAttributesQuery
import org.simplemodeling.model.value.DescriptiveAttributesQuery
import org.simplemodeling.model.value.LifecycleAttributesQuery
import org.simplemodeling.model.value.MediaAttributesQuery
import org.simplemodeling.model.value.NameAttributesQuery
import org.simplemodeling.model.value.PublicationAttributesQuery
import org.simplemodeling.model.value.ResourceAttributesQuery
import org.simplemodeling.model.value.SecurityAttributesQuery

/*
 * @since   Mar. 23, 2026
 *  version Mar. 29, 2026
 * @version Apr.  2, 2026
 * @author  ASAMI, Tomoharu
 */
abstract class SimpleObjectQuery {
  // NOTE:
  // This is completed first in the current style to prioritize execution.
  // A future phase will reorganize and integrate it toward the same
  // ValueObject-composition design direction as SimpleObject.
  def nameAttributes: NameAttributesQuery =
    NameAttributesQuery()

  def descriptiveAttributes: DescriptiveAttributesQuery =
    DescriptiveAttributesQuery()

  def lifecycleAttributes: LifecycleAttributesQuery =
    LifecycleAttributesQuery()

  def publicationAttributes: PublicationAttributesQuery =
    PublicationAttributesQuery()

  def securityAttributes: SecurityAttributesQuery =
    SecurityAttributesQuery()

  def resourceAttributes: ResourceAttributesQuery =
    ResourceAttributesQuery()

  def auditAttributes: AuditAttributesQuery =
    AuditAttributesQuery()

  def mediaAttributes: MediaAttributesQuery =
    MediaAttributesQuery()

  def contextualAttribute: ContextualAttributesQuery =
    ContextualAttributesQuery()
}
