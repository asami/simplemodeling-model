package org.simplemodeling.model.value

import java.time.ZonedDateTime

/*
 * @since   Aug.  1, 2025
 *  version Aug.  2, 2025
 *  version Mar. 29, 2026
 * @version Apr.  2, 2026
 *  version Aug.  2, 2025
 * @author  ASAMI, Tomoharu
 */
case class PublicationAttributes(
  publishAt: Option[ZonedDateTime],
  publicAt: Option[ZonedDateTime],
  closeAt: Option[ZonedDateTime],
  startAt: Option[ZonedDateTime],
  endAt: Option[ZonedDateTime]
)

object PublicationAttributes {
  trait Holder {
    def publicationAttributes: PublicationAttributes

    protected def publication_Attributes: PublicationAttributes = publicationAttributes
  }
}
