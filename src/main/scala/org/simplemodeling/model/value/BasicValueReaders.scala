package org.simplemodeling.model.value

import java.net.URL
import java.nio.charset.Charset
import java.time.{Instant, LocalDate, LocalDateTime, OffsetDateTime, ZoneId, ZonedDateTime}
import java.util.{Locale, TimeZone}
import scala.util.Try

import io.circe.{Decoder, Encoder}
import org.goldenport.Consequence
import org.goldenport.convert.ValueReader
import org.goldenport.datatype.MimeType
import org.goldenport.record.Record
import org.goldenport.schema.XString

/*
 * @since   Apr.  9, 2026
 *  version Apr. 25, 2026
 * @version May.  4, 2026
 * @author  ASAMI, Tomoharu
 */
object BasicValueReaders

given ValueReader[MimeType] =
  new ValueReader[MimeType] {
    def readC(v: Any): Consequence[MimeType] =
      v match {
        case m: MimeType => Consequence.success(m)
        case m: String if m.trim.nonEmpty => Consequence.success(MimeType(m.trim))
        case m: Record =>
          m.getString("value") match {
            case Some(s) => readC(s)
            case None => Consequence.failValueInvalid(v, XString)
          }
        case _ => Consequence.failValueInvalid(v, XString)
      }
  }

given ValueReader[Charset] =
  new ValueReader[Charset] {
    def readC(v: Any): Consequence[Charset] =
      v match {
        case m: Charset => Consequence.success(m)
        case m: String =>
          val s = m.trim
          if (s.isEmpty)
            Consequence.failValueInvalid(v, XString)
          else
            Try(Charset.forName(s)).toOption match {
              case Some(cs) => Consequence.success(cs)
              case None => Consequence.failValueInvalid(v, XString)
            }
        case m: Record =>
          m.getString("value") match {
            case Some(s) => readC(s)
            case None => Consequence.failValueInvalid(v, XString)
          }
        case _ => Consequence.failValueInvalid(v, XString)
      }
  }

given ValueReader[LocalDate] =
  new ValueReader[LocalDate] {
    def readC(v: Any): Consequence[LocalDate] =
      v match {
        case m: LocalDate => Consequence.success(m)
        case m: java.sql.Date => Consequence.success(m.toLocalDate)
        case m: String =>
          val s = m.trim
          if (s.isEmpty)
            Consequence.failValueInvalid(v, XString)
          else
            Try(LocalDate.parse(s)).toOption match {
              case Some(d) => Consequence.success(d)
              case None => Consequence.failValueInvalid(v, XString)
            }
        case m: Record =>
          m.getString("value") match {
            case Some(s) => readC(s)
            case None => Consequence.failValueInvalid(v, XString)
          }
        case _ => Consequence.failValueInvalid(v, XString)
      }
  }

given ValueReader[Instant] =
  new ValueReader[Instant] {
    def readC(v: Any): Consequence[Instant] =
      v match {
        case m: Instant => Consequence.success(m)
        case m: String =>
          val s = m.trim
          if (s.isEmpty)
            Consequence.failValueInvalid(v, XString)
          else
            Try(Instant.parse(s)).toOption match {
              case Some(d) => Consequence.success(d)
              case None => Consequence.failValueInvalid(v, XString)
            }
        case m: Record =>
          m.getString("value") match {
            case Some(s) => readC(s)
            case None => Consequence.failValueInvalid(v, XString)
          }
        case _ => Consequence.failValueInvalid(v, XString)
      }
  }

given ValueReader[ZonedDateTime] =
  new ValueReader[ZonedDateTime] {
    def readC(v: Any): Consequence[ZonedDateTime] =
      v match {
        case m: ZonedDateTime => Consequence.success(m)
        case m: OffsetDateTime => Consequence.success(m.toZonedDateTime)
        case m: Instant => Consequence.success(ZonedDateTime.ofInstant(m, ZoneId.systemDefault()))
        case m: LocalDateTime => Consequence.success(m.atZone(ZoneId.systemDefault()))
        case m: String =>
          val s = m.trim
          if (s.isEmpty)
            Consequence.failValueInvalid(v, XString)
          else
            Try(ZonedDateTime.parse(s)).toOption
              .orElse(Try(OffsetDateTime.parse(s).toZonedDateTime).toOption)
              .orElse(Try(Instant.parse(s)).toOption.map(ZonedDateTime.ofInstant(_, ZoneId.systemDefault()))) match {
                case Some(d) => Consequence.success(d)
                case None => Consequence.failValueInvalid(v, XString)
              }
        case m: Record =>
          m.getString("value") match {
            case Some(s) => readC(s)
            case None => Consequence.failValueInvalid(v, XString)
          }
        case _ => Consequence.failValueInvalid(v, XString)
      }
  }

given ValueReader[URL] =
  new ValueReader[URL] {
    def readC(v: Any): Consequence[URL] =
      v match {
        case m: URL => Consequence.success(m)
        case m: String =>
          val s = m.trim
          if (s.isEmpty)
            Consequence.failValueInvalid(v, XString)
          else
            Try(new URL(s)).toOption match {
              case Some(url) => Consequence.success(url)
              case None => Consequence.failValueInvalid(v, XString)
            }
        case m: Record =>
          m.getString("value") match {
            case Some(s) => readC(s)
            case None => Consequence.failValueInvalid(v, XString)
          }
        case _ => Consequence.failValueInvalid(v, XString)
      }
  }

given ValueReader[Locale] =
  new ValueReader[Locale] {
    def readC(v: Any): Consequence[Locale] =
      v match {
        case m: Locale => Consequence.success(m)
        case m: String =>
          val s = m.trim
          if (s.isEmpty)
            Consequence.failValueInvalid(v, XString)
          else
            Consequence.success(Locale.forLanguageTag(s.replace('_', '-')))
        case m: Record =>
          m.getString("value") match {
            case Some(s) => readC(s)
            case None => Consequence.failValueInvalid(v, XString)
          }
        case _ => Consequence.failValueInvalid(v, XString)
      }
  }

given ValueReader[TimeZone] =
  new ValueReader[TimeZone] {
    def readC(v: Any): Consequence[TimeZone] =
      v match {
        case m: TimeZone => Consequence.success(m)
        case m: String =>
          val s = m.trim
          if (s.isEmpty)
            Consequence.failValueInvalid(v, XString)
          else
            Consequence.success(TimeZone.getTimeZone(s))
        case m: Record =>
          m.getString("value") match {
            case Some(s) => readC(s)
            case None => Consequence.failValueInvalid(v, XString)
          }
        case _ => Consequence.failValueInvalid(v, XString)
      }
  }

given Encoder[LocalDate] = Encoder.encodeString.contramap(_.toString)
given Decoder[LocalDate] = Decoder.decodeString.emap { s =>
  Try(LocalDate.parse(s)).toEither.left.map(_ => s"Invalid LocalDate: $s")
}

given Encoder[Instant] = Encoder.encodeString.contramap(_.toString)
given Decoder[Instant] = Decoder.decodeString.emap { s =>
  Try(Instant.parse(s)).toEither.left.map(_ => s"Invalid Instant: $s")
}

given Encoder[ZonedDateTime] = Encoder.encodeString.contramap(_.toString)
given Decoder[ZonedDateTime] = Decoder.decodeString.emap { s =>
  Try(ZonedDateTime.parse(s)).toEither.left.map(_ => s"Invalid ZonedDateTime: $s")
}

given Encoder[URL] = Encoder.encodeString.contramap(_.toExternalForm)
given Decoder[URL] = Decoder.decodeString.emap { s =>
  Try(new URL(s)).toEither.left.map(_ => s"Invalid URL: $s")
}

given Encoder[Locale] = Encoder.encodeString.contramap(_.toLanguageTag)
given Decoder[Locale] = Decoder.decodeString.map(s => Locale.forLanguageTag(s.replace('_', '-')))

given Encoder[TimeZone] = Encoder.encodeString.contramap(_.getID)
given Decoder[TimeZone] = Decoder.decodeString.map(TimeZone.getTimeZone)
