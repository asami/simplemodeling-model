package org.simplemodeling.model.value

final case class NonEmptyString private (value: String) extends ValueObject

object NonEmptyString {
  def from(value: String): Either[String, NonEmptyString] =
    if (value != null && value.nonEmpty) Right(NonEmptyString(value))
    else Left("value must be non-empty")
}
