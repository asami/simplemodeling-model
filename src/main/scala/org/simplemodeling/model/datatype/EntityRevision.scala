package org.simplemodeling.model.datatype

import io.circe.{Codec, Decoder, Encoder}
import org.goldenport.Consequence
import org.goldenport.convert.ValueReader
import org.goldenport.schema.{DataType, XPositiveInteger}
import org.simplemodeling.model.value.NominalScalar

/*
 * Framework-managed revision of one persisted Entity state.
 *
 * @since   Jul. 25, 2026
 * @version Jul. 25, 2026
 * @author  ASAMI, Tomoharu
 */
final class EntityRevision private (val value: Long) extends NominalScalar {
  require(value >= EntityRevision.MINIMUM, s"revision must be at least ${EntityRevision.MINIMUM}: $value")

  def nextC: Consequence[EntityRevision] =
    if value == EntityRevision.MAXIMUM then
      Consequence.argumentFieldLimitExceeded(
        "revision",
        EntityRevision.MAXIMUM,
        BigInt(value) + 1,
        EntityRevision._advancement_policy
      )
    else
      EntityRevision.createC(value + 1)

  override def equals(other: Any): Boolean =
    other match
      case that: EntityRevision => value == that.value
      case _ => false

  override def hashCode(): Int = java.lang.Long.hashCode(value)

  override def toString: String = s"EntityRevision($value)"
}

object EntityRevision {
  val MINIMUM: Long = 1L
  val MAXIMUM: Long = Long.MaxValue
  val INITIAL: EntityRevision = new EntityRevision(MINIMUM)
  val DATA_TYPE: DataType = XPositiveInteger

  private val _expected = s"an integer from $MINIMUM through $MAXIMUM"
  private val _advancement_policy = "entity.revision.advance"

  given ValueReader[EntityRevision] with
    def readC(value: Any): Consequence[EntityRevision] =
      value match
        case revision: EntityRevision => Consequence.success(revision)
        case input => summon[ValueReader[Long]].readC(input).flatMap(x => createC(x))

  given Codec[EntityRevision] = Codec.from(
    Decoder.decodeLong.emap { value =>
      createC(value) match
        case Consequence.Success(revision) => Right(revision)
        case Consequence.Failure(conclusion) => Left(conclusion.display)
    },
    Encoder.encodeLong.contramap(_.value)
  )

  def createC(value: Long): Consequence[EntityRevision] =
    if value >= MINIMUM then
      Consequence.success(new EntityRevision(value))
    else
      Consequence.argumentFieldInvalid("revision", _expected, value)

  def createC(value: Any): Consequence[EntityRevision] =
    summon[ValueReader[EntityRevision]].readC(value)

  def parseC(value: String): Consequence[EntityRevision] =
    summon[ValueReader[Long]].readC(value).flatMap(x => createC(x))

  def unapply(revision: EntityRevision): Option[Long] = Option(revision).map(_.value)
}
