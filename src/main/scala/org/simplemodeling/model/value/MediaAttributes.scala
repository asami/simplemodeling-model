package org.simplemodeling.model.value

import java.net.URL

/*
 * @since   Aug.  2, 2025
 *  version Mar. 29, 2026
 * @version Apr.  2, 2026
 * @author  ASAMI, Tomoharu
 */
case class MediaAttributes(
  url: Option[URL],
  images: Vector[Image],
  audios: Vector[Audio],
  videos: Vector[Video],
  atathments: Vector[Attachment]
)

object MediaAttributes {
  trait Holder {
    def mediaAttributes: MediaAttributes

    protected def media_Attributes: MediaAttributes = mediaAttributes
  }
}
