package org.simplemodeling.model.value

import java.time.ZonedDateTime
import scala.util.Try
import org.goldenport.Consequence
import org.goldenport.convert.ValueReader
import org.goldenport.record.Record
import org.simplemodeling.model.statemachine.ActivationStatus

/*
 * @since   Aug.  1, 2025
 *  version Aug.  2, 2025
 *  version Mar. 29, 2026
 * @version Apr. 13, 2026
 * @author  ASAMI, Tomoharu
 */
case class ResourceAttributes(
  activatedAt: Option[ZonedDateTime] = None,
  deactivatedAt: Option[ZonedDateTime] = None,
  expiresAt: Option[ZonedDateTime] = None,
  activationStatus: ActivationStatus = ActivationStatus.default
)

object ResourceAttributes {
  given ValueReader[ResourceAttributes] with
    def readC(v: Any): Consequence[ResourceAttributes] = v match
      case m: ResourceAttributes => Consequence.success(m)
      case m: Record =>
        for {
          activatedat <- _get_zoned_date_time(m, "activatedAt", "activated_at")
          deactivatedat <- _get_zoned_date_time(m, "deactivatedAt", "deactivated_at")
          expiresat <- _get_zoned_date_time(m, "expiresAt", "expires_at")
          activationstatus <- _get_activation_status(m, "activationStatus", "activation_status")
        } yield ResourceAttributes(
          activatedAt = activatedat,
          deactivatedAt = deactivatedat,
          expiresAt = expiresat,
          activationStatus = activationstatus.getOrElse(ActivationStatus.default)
        )
      case _ =>
        Consequence.failValueInvalid(v, org.goldenport.schema.XString)

  trait Holder {
    def resourceAttributes: ResourceAttributes

    protected def resource_Attributes: ResourceAttributes = resourceAttributes
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

  private def _get_activation_status(record: Record, keys: String*): Consequence[Option[ActivationStatus]] =
    _get_as[ActivationStatus](record, keys)

  private def _get_as[A](
    record: Record,
    keys: Seq[String]
  )(using vr: ValueReader[A]): Consequence[Option[A]] =
    keys.foldLeft(Consequence.success(Option.empty[A])) { (z, key) =>
      z.flatMap {
        case s @ Some(_) => Consequence.success(s)
        case None => record.getAsC[A](key)
      }
    }
}
