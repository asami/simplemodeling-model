package org.simplemodeling.model

import org.simplemodeling.model.value.AuditAttributesUpdate
import org.simplemodeling.model.value.ContextualAttributesUpdate
import org.simplemodeling.model.value.DescriptiveAttributesUpdate
import org.simplemodeling.model.value.LifecycleAttributesUpdate
import org.simplemodeling.model.value.MediaAttributesUpdate
import org.simplemodeling.model.value.NameAttributesUpdate
import org.simplemodeling.model.value.PublicationAttributesUpdate
import org.simplemodeling.model.value.ResourceAttributesUpdate
import org.simplemodeling.model.value.SecurityAttributesUpdate

/*
 * @since   Mar. 23, 2026
 *  version Mar. 29, 2026
 * @version Apr.  2, 2026
 * @author  ASAMI, Tomoharu
 */
abstract class SimpleObjectUpdate {
  // NOTE:
  // Execution is prioritized, so this current style is adopted as the
  // temporary completed form.
  // In a later phase, this will be moved toward a more consistent
  // ValueObject-composition model, aligned with SimpleObject itself.
  def nameAttributes: NameAttributesUpdate =
    NameAttributesUpdate()

  def descriptiveAttributes: DescriptiveAttributesUpdate =
    DescriptiveAttributesUpdate()

  def lifecycleAttributes: LifecycleAttributesUpdate =
    LifecycleAttributesUpdate()

  def publicationAttributes: PublicationAttributesUpdate =
    PublicationAttributesUpdate()

  def securityAttributes: SecurityAttributesUpdate =
    SecurityAttributesUpdate()

  def resourceAttributes: ResourceAttributesUpdate =
    ResourceAttributesUpdate()

  def auditAttributes: AuditAttributesUpdate =
    AuditAttributesUpdate()

  def mediaAttributes: MediaAttributesUpdate =
    MediaAttributesUpdate()

  def contextualAttribute: ContextualAttributesUpdate =
    ContextualAttributesUpdate()
}
