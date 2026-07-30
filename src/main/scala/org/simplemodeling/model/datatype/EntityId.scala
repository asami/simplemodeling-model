package org.simplemodeling.model.datatype

import org.goldenport.Consequence
import java.time.Instant
import io.circe.{Codec, Decoder, Encoder}
import org.goldenport.id.{CompactUuid, UniversalId}
import org.goldenport.convert.ValueReader
import org.goldenport.record.Record

private object EntityCollectionIdPayload {
  private val _prefix = "ec1_"

  def encode(major: String, minor: String, name: String): String =
    s"${_prefix}${major.length}_${major}_${minor.length}_${minor}_${name.length}_${name}"

  def decode(payload: String): Consequence[(String, String, String)] =
    if (!payload.startsWith(_prefix))
      _failure("unsupported version")
    else
      for {
        majorpair <- _component(payload, _prefix.length, false)
        minorpair <- _component(payload, majorpair._2, false)
        namepair <- _component(payload, minorpair._2, true)
      } yield (majorpair._1, minorpair._1, namepair._1)

  private def _component(
    payload: String,
    offset: Int,
    isfinal: Boolean
  ): Consequence[(String, Int)] = {
    val separator = payload.indexOf('_', offset)
    if (separator < 0)
      _failure("missing length separator")
    else {
      val lengthlabel = payload.substring(offset, separator)
      _length(lengthlabel).flatMap { length =>
        val start = separator + 1
        if (length > payload.length - start)
          _failure("truncated component")
        else {
          val end = start + length
          val component = payload.substring(start, end)
          val validending =
            if (isfinal)
              end == payload.length
            else
              end < payload.length && payload.charAt(end) == '_'
          if (!isLabel(component))
            _failure("invalid component label")
          else if (!validending)
            _failure("missing component separator or trailing input")
          else
            Consequence.success(component -> (if (isfinal) end else end + 1))
        }
      }
    }
  }

  private def _length(lengthlabel: String): Consequence[Int] =
    if (lengthlabel.isEmpty)
      _failure("empty component length")
    else if (lengthlabel.length > 1 && lengthlabel.head == '0')
      _failure("noncanonical component length")
    else if (!lengthlabel.forall(_.isDigit))
      _failure("nondecimal component length")
    else {
      val result = lengthlabel.foldLeft(Option(0)) { (acc, char) =>
        acc.flatMap { current =>
          val digit = char - '0'
          if (current > (Int.MaxValue - digit) / 10)
            None
          else
            Some(current * 10 + digit)
        }
      }
      result match {
        case Some(length) if length > 0 => Consequence.success(length)
        case Some(_) => _failure("nonpositive component length")
        case None => _failure("component length overflow")
      }
    }

  def isLabel(value: String): Boolean =
    value != null && value.matches("[A-Za-z][A-Za-z0-9_]*")

  private def _failure(reason: String): Consequence[Nothing] =
    Consequence.valueFormatError(s"Invalid EntityCollectionId payload: $reason")
}

/*
 * @since   Apr. 11, 2025
 *  version Feb. 27, 2026
 *  version Mar. 31, 2026
 *  version May.  1, 2026
 * @version Jul. 30, 2026
 * @author  ASAMI, Tomoharu
 */
final case class EntityId(
  major: String,
  minor: String,
  collection: EntityCollectionId,
  timestamp: Option[Instant] = Some(Instant.ofEpochMilli(Instant.now().toEpochMilli)),
  entropy: Option[String] = Some(CompactUuid.generateString())
) extends UniversalId(
  major,
  minor,
  "entity",
  EntityCollectionIdPayload.encode(collection.major, collection.minor, collection.name),
  timestamp,
  entropy
) {
  require(timestamp.nonEmpty, "EntityId timestamp must be materialized")
  require(timestamp.exists(EntityId._is_canonical_timestamp), "EntityId timestamp must be a nonnegative exact epoch millisecond")
  require(entropy.exists(EntityId._is_canonical_entropy), "EntityId entropy must be nonempty and delimiter-safe")

  override def equals(obj: Any): Boolean =
    obj match {
      case that: EntityId => value == that.value
      case _ =>
        false
    }

  override def hashCode(): Int =
    value.##
}

object EntityId {
  private val _record_keys = Vector("id", "entityId", "entity_id")
  private val _entropy_pattern = "[A-Za-z0-9_]+"

  given Codec[EntityId] = Codec.from(
    Decoder.decodeString.emap(value => parse(value).toOption.toRight(s"Invalid EntityId value: '$value'")),
    Encoder.encodeString.contramap(_.value)
  )

  given ValueReader[EntityId] with {
    def readC(v: Any): Consequence[EntityId] = Option(v) match {
      case None => Consequence.failure("Invalid EntityId value: null")
      case Some(value) => value match {
        case id: EntityId => Consequence.success(id)
        case Some(id: EntityId) => Consequence.success(id)
        case Some(s: String) => parse(s)
        case Some(other) => readC(other)
        case record: Record => createC(record)
        case s: String => parse(s)
        case other => parse(other.toString)
      }
    }
  }

  def createC(p: Record): Consequence[EntityId] =
    _record_keys.view.flatMap(p.getAny).headOption match {
      case Some(id: EntityId) => Consequence.success(id)
      case Some(record: Record) => createC(record)
      case Some(v: String) if v.nonEmpty => parse(v)
      case Some(v) if v.toString.trim.nonEmpty => parse(v.toString.trim)
      case Some(_) => Consequence.failure("Invalid EntityId value: empty")
      case None => _structured_record(p)
    }

  def parse(s: String): Consequence[EntityId] =
    if (s == null)
      Consequence.valueInvalid("Invalid EntityId value: null")
    else
      UniversalId.parseParts(s, "entity").flatMap { parts =>
        parts.subkind match {
          case Some(payload) =>
            EntityCollectionIdPayload.decode(payload).flatMap { collectionparts =>
              _create(
                major = parts.major,
                minor = parts.minor,
                collection = EntityCollectionId(
                  collectionparts._1,
                  collectionparts._2,
                  collectionparts._3
                ),
                timestamp = parts.timestamp,
                entropy = parts.entropy
              )
            }
          case None =>
            Consequence.valueFormatError(s"Invalid EntityId format: missing collection payload in '$s'")
        }
      }

  private def _structured_record(p: Record): Consequence[EntityId] =
    for {
      major <- p.getString("major").map(Consequence.success).getOrElse(Consequence.failure("Invalid EntityId record: missing major"))
      minor <- p.getString("minor").map(Consequence.success).getOrElse(Consequence.failure("Invalid EntityId record: missing minor"))
      collection <- _collection(p)
      timestamp <- _timestamp(p)
      entropy <- _entropy(p)
      identity <- _create(major, minor, collection, timestamp, entropy)
    } yield identity

  private def _collection(p: Record): Consequence[EntityCollectionId] =
    p.getAny("collection") match {
      case Some(id: EntityCollectionId) => Consequence.success(id)
      case Some(record: Record) => EntityCollectionId.createC(record)
      case Some(s: String) => EntityCollectionId.parse(s)
      case Some(other) => EntityCollectionId.parse(other.toString)
      case None => Consequence.failure("Invalid EntityId record: missing complete collection")
    }

  private def _timestamp(p: Record): Consequence[Instant] =
    p.getString("timestamp") match {
      case Some(value) =>
        scala.util.Try(Instant.parse(value)).toOption
          .map(Consequence.success)
          .getOrElse(Consequence.valueInvalid("Invalid EntityId record: malformed timestamp"))
      case None =>
        Consequence.failure("Invalid EntityId record: missing timestamp")
    }

  private def _entropy(p: Record): Consequence[String] =
    p.getString("entropy") match {
      case Some(value) if _is_canonical_entropy(value) => Consequence.success(value)
      case Some(_) => Consequence.valueInvalid("Invalid EntityId record: malformed entropy")
      case None => Consequence.failure("Invalid EntityId record: missing entropy")
    }

  private def _create(
    major: String,
    minor: String,
    collection: EntityCollectionId,
    timestamp: Instant,
    entropy: String
  ): Consequence[EntityId] = {
    if (!_is_canonical_timestamp(timestamp))
      Consequence.valueInvalid("Invalid EntityId timestamp: expected a nonnegative exact epoch millisecond")
    else if (!_is_canonical_entropy(entropy))
      Consequence.valueInvalid("Invalid EntityId entropy: expected nonempty alphanumeric or underscore")
    else
      Consequence.success(EntityId(major, minor, collection, Some(timestamp), Some(entropy)))
  }

  private def _is_canonical_entropy(value: String): Boolean =
    value.matches(_entropy_pattern)

  private def _is_canonical_timestamp(value: Instant): Boolean =
    scala.util.Try {
      val millis = value.toEpochMilli
      millis >= 0 && Instant.ofEpochMilli(millis) == value
    }.getOrElse(false)
}

final case class EntityCollectionId(
  major: String,
  minor: String,
  name: String,
) extends UniversalId(
  major,
  minor,
  "entity_collection",
  EntityCollectionIdPayload.encode(major, minor, name),
  Some(org.goldenport.id.UniversalId.StableTimestamp),
  Some(org.goldenport.id.UniversalId.StableEntropy)
) {
  require(EntityCollectionIdPayload.isLabel(name), "EntityCollectionId name must be a label")
}

object EntityCollectionId {
  given Codec[EntityCollectionId] = Codec.from(
    Decoder.decodeString.emap(value => parse(value).toOption.toRight(s"Invalid EntityCollectionId value: '$value'")),
    Encoder.encodeString.contramap(_.value)
  )

  given ValueReader[EntityCollectionId] with {
    def readC(v: Any): Consequence[EntityCollectionId] = Option(v) match {
      case None => Consequence.failure("Invalid EntityCollectionId value: null")
      case Some(value) => value match {
        case id: EntityCollectionId => Consequence.success(id)
        case Some(id: EntityCollectionId) => Consequence.success(id)
        case Some(s: String) => parse(s)
        case Some(other) => readC(other)
        case record: Record => createC(record)
        case s: String => parse(s)
        case other => parse(other.toString)
      }
    }
  }

  def createC(p: Record): Consequence[EntityCollectionId] =
    for {
      major <- p.getString("major").map(Consequence.success).getOrElse(Consequence.failure("Invalid EntityCollectionId record: missing major"))
      minor <- p.getString("minor").map(Consequence.success).getOrElse(Consequence.failure("Invalid EntityCollectionId record: missing minor"))
      name <- p.getString("name").orElse(p.getString("collectionName")).orElse(p.getString("collection_name")).map(Consequence.success).getOrElse(Consequence.failure("Invalid EntityCollectionId record: missing name"))
      collection <- _create(major, minor, name)
    } yield collection

  private def _create(
    major: String,
    minor: String,
    name: String
  ): Consequence[EntityCollectionId] =
    if (!EntityCollectionIdPayload.isLabel(name))
      Consequence.valueInvalid("Invalid EntityCollectionId name: expected a label")
    else
      Consequence.success(EntityCollectionId(major, minor, name))

  def parse(s: String): Consequence[EntityCollectionId] =
    if (s == null)
      Consequence.valueInvalid("Invalid EntityCollectionId value: null")
    else
      UniversalId.parseParts(s, "entity_collection").flatMap { parts =>
        if (parts.timestamp != UniversalId.StableTimestamp || parts.entropy != UniversalId.StableEntropy)
          Consequence.valueInvalid("EntityCollectionId requires the canonical stable timestamp and entropy")
        else
          parts.subkind match {
            case Some(payload) =>
              EntityCollectionIdPayload.decode(payload).flatMap { collectionparts =>
                if (collectionparts._1 != parts.major || collectionparts._2 != parts.minor)
                  Consequence.valueInvalid("EntityCollectionId outer namespace does not match its payload")
                else
                  Consequence.success(
                    EntityCollectionId(collectionparts._1, collectionparts._2, collectionparts._3)
                  )
              }
            case None =>
              Consequence.valueFormatError(s"Invalid EntityCollectionId format: missing collection payload in '$s'")
          }
      }
}

final case class AggregateCollectionId(
  major: String,
  minor: String,
  name: String,
) extends UniversalId(
  major,
  minor,
  "aggregate_collection",
  name,
  Some(org.goldenport.id.UniversalId.StableTimestamp),
  Some(org.goldenport.id.UniversalId.StableEntropy)
) derives Codec.AsObject

object AggregateCollectionId {
  given ValueReader[AggregateCollectionId] with {
    def readC(v: Any): Consequence[AggregateCollectionId] = Option(v) match {
      case None => Consequence.failure("Invalid AggregateCollectionId value: null")
      case Some(value) => value match {
        case id: AggregateCollectionId => Consequence.success(id)
        case s: String => parse(s)
        case other => Consequence.valueInvalid(s"Invalid AggregateCollectionId value type: ${other.getClass.getName}")
      }
    }
  }

  def parse(s: String): Consequence[AggregateCollectionId] =
    _parse(s, "aggregate_collection", AggregateCollectionId.apply, "AggregateCollectionId")
}

final case class ViewCollectionId(
  major: String,
  minor: String,
  name: String,
) extends UniversalId(
  major,
  minor,
  "view_collection",
  name,
  Some(org.goldenport.id.UniversalId.StableTimestamp),
  Some(org.goldenport.id.UniversalId.StableEntropy)
) derives Codec.AsObject

object ViewCollectionId {
  given ValueReader[ViewCollectionId] with {
    def readC(v: Any): Consequence[ViewCollectionId] = Option(v) match {
      case None => Consequence.failure("Invalid ViewCollectionId value: null")
      case Some(value) => value match {
        case id: ViewCollectionId => Consequence.success(id)
        case s: String => parse(s)
        case other => Consequence.valueInvalid(s"Invalid ViewCollectionId value type: ${other.getClass.getName}")
      }
    }
  }

  def parse(s: String): Consequence[ViewCollectionId] =
    _parse(s, "view_collection", ViewCollectionId.apply, "ViewCollectionId")
}

private def _parse[A](
  value: String,
  kind: String,
  build: (String, String, String) => A,
  label: String
): Consequence[A] =
  if (value == null)
    Consequence.valueInvalid(s"Invalid $label value: null")
  else
    UniversalId.parseParts(value, kind).flatMap { parts =>
      if (parts.timestamp != UniversalId.StableTimestamp || parts.entropy != UniversalId.StableEntropy)
        Consequence.valueInvalid(s"Invalid $label format: expected canonical stable timestamp and entropy")
      else
        parts.subkind match {
          case Some(name) if name.nonEmpty => Consequence.success(build(parts.major, parts.minor, name))
          case _ => Consequence.valueFormatError(s"Invalid $label format: missing name in '$value'")
        }
    }
