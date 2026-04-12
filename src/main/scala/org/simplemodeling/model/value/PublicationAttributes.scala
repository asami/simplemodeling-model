package org.simplemodeling.model.value

import java.time.ZonedDateTime
import scala.util.Try
import org.goldenport.Consequence
import org.goldenport.convert.ValueReader
import org.goldenport.record.Record

/*
 * @since   Aug.  1, 2025
 *  version Aug.  2, 2025
 *  version Mar. 29, 2026
 * @version Apr. 13, 2026
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
  given ValueReader[PublicationAttributes] with
    def readC(v: Any): Consequence[PublicationAttributes] = v match
      case m: PublicationAttributes => Consequence.success(m)
      case m: Record =>
        for {
          publishat <- _get_zoned_date_time(m, "publishAt", "publish_at")
          publicat <- _get_zoned_date_time(m, "publicAt", "public_at")
          closeat <- _get_zoned_date_time(m, "closeAt", "close_at")
          startat <- _get_zoned_date_time(m, "startAt", "start_at")
          endat <- _get_zoned_date_time(m, "endAt", "end_at")
        } yield PublicationAttributes(publishat, publicat, closeat, startat, endat)
      case _ =>
        Consequence.failValueInvalid(v, org.goldenport.schema.XString)

  trait Holder {
    def publicationAttributes: PublicationAttributes

    protected def publication_Attributes: PublicationAttributes = publicationAttributes
  }

  private def _get_zoned_date_time(record: Record, keys: String*): Consequence[Option[ZonedDateTime]] =
    keys.iterator.flatMap(record.getAny).map {
      case m: ZonedDateTime => Consequence.success(Some(m))
      case m: java.time.OffsetDateTime => Consequence.success(Some(m.toZonedDateTime))
      case m: java.time.Instant => Consequence.success(Some(ZonedDateTime.ofInstant(m, java.time.ZoneOffset.UTC)))
      case m: String if m.trim.isEmpty => Consequence.success(None)
      case m: String =>
        Try(ZonedDateTime.parse(m.trim)).toOption match
          case Some(z) => Consequence.success(Some(z))
          case None => Consequence.failValueInvalid(m, org.goldenport.schema.XString)
      case m => Consequence.failValueInvalid(m, org.goldenport.schema.XString)
    }.toVector.headOption.getOrElse(Consequence.success(None))
}
