package org.simplemodeling.model.datatype

import org.goldenport.Consequence
import org.goldenport.id.UniversalId
import org.goldenport.convert.ValueReader
import org.goldenport.record.Record
import io.circe.Codec

/*
 * @since   Apr. 11, 2025
 *  version Feb. 27, 2026
 *  version Mar. 31, 2026
 * @version May.  1, 2026
 * @author  ASAMI, Tomoharu
 */
final case class EntityId(
  major: String,
  minor: String,
  collection: EntityCollectionId,
  timestamp: Option[java.time.Instant] = None,
  entropy: Option[String] = None
) extends UniversalId(major, minor, "entity", collection.name, timestamp, entropy) derives Codec.AsObject {
  // Canonical machine-facing identifier string is `value`.
  // Use `value` for persistence keys, lookup keys, joins, query parameters, and map keys.
  // `print` is presentation-oriented, while `show` / `toString` are debugger-oriented summaries.
}

object EntityId {
  private val _record_keys = Vector("id", "entityId", "entity_id")

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

  def parse(s: String): Consequence[EntityId] = {
    UniversalId.parseParts(s, "entity").flatMap { parts =>
      parts.subkind match {
        case Some(name) =>
          Consequence.success(
            EntityId(
              major = parts.major,
              minor = parts.minor,
              collection = EntityCollectionId(parts.major, parts.minor, name),
              timestamp = Some(parts.timestamp),
              entropy = Some(parts.entropy)
            )
          )
        case None =>
          Consequence.failure(s"Invalid EntityId format: missing collection name in '$s'")
      }
    }
  }

  private def _structured_record(p: Record): Consequence[EntityId] =
    for {
      major <- p.getString("major").map(Consequence.success).getOrElse(Consequence.failure("Invalid EntityId record: missing major"))
      minor <- p.getString("minor").map(Consequence.success).getOrElse(Consequence.failure("Invalid EntityId record: missing minor"))
      collection <- _collection(p)
    } yield EntityId(
      major = major,
      minor = minor,
      collection = collection,
      timestamp = p.getString("timestamp").flatMap(x => scala.util.Try(java.time.Instant.parse(x)).toOption),
      entropy = p.getString("entropy")
    )

  private def _collection(p: Record): Consequence[EntityCollectionId] =
    p.getAny("collection") match {
      case Some(id: EntityCollectionId) => Consequence.success(id)
      case Some(record: Record) => EntityCollectionId.createC(record)
      case Some(s: String) => EntityCollectionId.parse(s)
      case Some(other) => EntityCollectionId.parse(other.toString)
      case None =>
        p.getString("collectionName")
          .orElse(p.getString("collection_name"))
          .orElse(p.getString("subkind"))
          .map(name => Consequence.success(EntityCollectionId(p.getString("major").getOrElse(""), p.getString("minor").getOrElse(""), name)))
          .getOrElse(Consequence.failure("Invalid EntityId record: missing collection"))
    }
}

final case class EntityCollectionId(
  major: String,
  minor: String,
  name: String,
) extends UniversalId(
  major,
  minor,
  "entity_collection",
  name,
  Some(org.goldenport.id.UniversalId.StableTimestamp),
  Some(org.goldenport.id.UniversalId.StableEntropy)
) derives Codec.AsObject

object EntityCollectionId {
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
    } yield EntityCollectionId(major, minor, name)

  def parse(s: String): Consequence[EntityCollectionId] =
    UniversalId.parseParts(s, "entity_collection").map { parts =>
      EntityCollectionId(parts.major, parts.minor, parts.subkind.getOrElse(parts.minor))
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
        case s: String => ???
        case other => ???
      }
    }
  }
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
        case s: String => ???
        case other => ???
      }
    }
  }
}
