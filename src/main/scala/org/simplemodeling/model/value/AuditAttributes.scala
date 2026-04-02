package org.simplemodeling.model.value

/*
 * @since   Aug.  1, 2025
 *  version Aug.  2, 2025
 *  version Mar. 29, 2026
 * @version Apr.  2, 2026
 *  version Aug.  2, 2025
 * @author  ASAMI, Tomoharu
 */
case class AuditAttributes(
)

object AuditAttributes {
  trait Holder {
    def auditAttributes: AuditAttributes

    protected def audit_Attributes: AuditAttributes = auditAttributes
  }
}
