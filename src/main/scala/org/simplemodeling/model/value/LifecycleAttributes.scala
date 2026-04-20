package org.simplemodeling.model.value

import java.time.ZonedDateTime
import scala.util.Try
import org.goldenport.Consequence
import org.goldenport.convert.ValueReader
import org.goldenport.datatype.Identifier
import org.goldenport.record.Record
import org.simplemodeling.model.statemachine.PostStatus
import org.simplemodeling.model.statemachine.Aliveness

/*
 * @since   Aug.  1, 2025
 *  version Aug.  4, 2025
 *  version Feb. 19, 2026
 *  version Mar. 29, 2026
 * @version Apr. 20, 2026
 * @author  ASAMI, Tomoharu
 */
case class LifecycleAttributes(
  createdAt: ZonedDateTime,
  updatedAt: Option[ZonedDateTime],
  createdBy: Identifier,
  updatedBy: Option[Identifier],
  postStatus: PostStatus,
  aliveness: Aliveness
)

object LifecycleAttributes {
  given ValueReader[LifecycleAttributes] with
    def readC(v: Any): Consequence[LifecycleAttributes] = v match
      case m: LifecycleAttributes => Consequence.success(m)
      case m: Record =>
        for {
          createdat <- _get_zoned_date_time(m, "createdAt", "created_at")
          updatedat <- _get_zoned_date_time(m, "updatedAt", "updated_at")
          createdby <- _get_identifier(m, "createdBy", "created_by")
          updatedby <- _get_identifier(m, "updatedBy", "updated_by")
          poststatus <- _get_post_status(m, "postStatus", "post_status")
          aliveness <- _get_aliveness(m, "aliveness")
        } yield LifecycleAttributes(
          createdAt = createdat.getOrElse(LifecycleAttributes.defaultCreatedAt),
          updatedAt = updatedat,
          createdBy = createdby.getOrElse(Identifier("system")),
          updatedBy = updatedby,
          postStatus = poststatus.getOrElse(PostStatus.default),
          aliveness = aliveness.getOrElse(Aliveness.default)
        )
      case _ =>
        Consequence.failValueInvalid(v, org.goldenport.schema.XString)

  val defaultCreatedAt: ZonedDateTime =
    ZonedDateTime.parse("1970-01-01T00:00:00Z")

  trait Holder {
    def lifecycleAttributes: LifecycleAttributes

    protected def lifecycle_Attributes: LifecycleAttributes = lifecycleAttributes
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

  private def _get_identifier(record: Record, keys: String*): Consequence[Option[Identifier]] =
    keys.foldLeft(Consequence.success(Option.empty[Identifier])) { (z, key) =>
      z.flatMap {
        case s @ Some(_) => Consequence.success(s)
        case None =>
          record.getAny(key) match {
            case Some(m: Identifier) => Consequence.success(Some(m))
            case Some(m: String) if m.trim.isEmpty => Consequence.success(None)
            case Some(m: String) => Consequence.success(Some(Identifier(_identifier_text(m))))
            case Some(m) => summon[ValueReader[Identifier]].readC(m).map(Some(_))
            case None => Consequence.success(None)
          }
      }
    }

  private def _identifier_text(text: String): String = {
    val sanitized = text.trim.map {
      case c if c.isLetterOrDigit || c == '_' => c
      case _ => '_'
    }.mkString
    val nonempty = if (sanitized.isEmpty) "unknown" else sanitized
    if (nonempty.headOption.exists(_.isLetter))
      nonempty
    else
      s"id_$nonempty"
  }

  private def _get_post_status(record: Record, keys: String*): Consequence[Option[PostStatus]] =
    _get_as[PostStatus](record, keys)

  private def _get_aliveness(record: Record, keys: String*): Consequence[Option[Aliveness]] =
    _get_as[Aliveness](record, keys)

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
