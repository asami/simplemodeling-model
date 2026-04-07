package org.simplemodeling.model.value

import java.time.ZonedDateTime
import org.simplemodeling.model.statemachine.ActivationStatus

/*
 * @since   Aug.  1, 2025
 *  version Aug.  2, 2025
 *  version Mar. 29, 2026
 * @version Apr.  7, 2026
 *  version Aug.  2, 2025
 * @author  ASAMI, Tomoharu
 */
case class ResourceAttributes(
  activatedAt: Option[ZonedDateTime] = None,
  deactivatedAt: Option[ZonedDateTime] = None,
  expiresAt: Option[ZonedDateTime] = None,
  activationStatus: ActivationStatus = ActivationStatus.default
)

object ResourceAttributes {
  trait Holder {
    def resourceAttributes: ResourceAttributes

    protected def resource_Attributes: ResourceAttributes = resourceAttributes
  }
}
