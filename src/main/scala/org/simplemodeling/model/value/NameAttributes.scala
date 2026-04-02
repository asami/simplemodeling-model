package org.simplemodeling.model.value

import org.goldenport.datatype.{I18nLabel, I18nTitle, Name}
import org.goldenport.convert.ValueReader
import org.goldenport.Consequence
import org.goldenport.record.Record
import org.goldenport.schema.XString

/*
 * @since   Aug.  1, 2025
 *  version Aug.  2, 2025
 *  version Dec. 30, 2025
 *  version Jan. 21, 2026
 *  version Mar. 29, 2026
 *  version Apr.  2, 2026
 * @version Apr.  3, 2026
 * @author  ASAMI, Tomoharu
 */
type NameAttributes = org.goldenport.value.NameAttributes

object NameAttributes {
  export org.goldenport.value.NameAttributes.BareHolder

  def simple(name: String): NameAttributes =
    org.goldenport.value.NameAttributes.simple(name)

  def simple(name: org.goldenport.datatype.Name): NameAttributes =
    org.goldenport.value.NameAttributes.simple(name)

  final case class Builder(
    nameAttributes: Option[NameAttributes] = None,
    name: Option[Name] = None,
    label: Option[I18nLabel] = None,
    title: Option[I18nTitle] = None
  ) {
    def withNameAttributes(p: NameAttributes): Builder = copy(nameAttributes = Some(p))
    def withName(p: Name): Builder = copy(name = Some(p))
    def withName(p: String): Builder = copy(name = Some(Name(p)))
    def withLabel(p: I18nLabel): Builder = copy(label = Some(p))
    def withTitle(p: I18nTitle): Builder = copy(title = Some(p))
    def withTitle(p: String): Builder = copy(title = Some(I18nTitle(p)))

    def build(): NameAttributes = {
      val base = nameAttributes.getOrElse(name.map(simple).getOrElse(simple(Name("unknown"))))
      val withlabel = label.fold(base)(x => base.copy(label = Some(x)))
      title.fold(withlabel)(x => withlabel.copy(title = Some(x)))
    }
  }

  object Builder {
    def apply(p: NameAttributes): Builder =
      Builder(nameAttributes = Some(p))
  }

  private[value] def _string_value(p: Record, key: String): Option[String] =
    p.getAny(key).collect { case s: String => s }

  trait Holder {
    def nameAttributes: NameAttributes

    protected def name_Attributes: NameAttributes = nameAttributes

    def name = nameAttributes.name
    def label = nameAttributes.label
  }
}

given ValueReader[NameAttributes] =
  new ValueReader[NameAttributes] {
    def readC(v: Any): Consequence[NameAttributes] = v match {
      case m: org.goldenport.value.NameAttributes => Consequence.success(m)
      case m: Record =>
        val b = NameAttributes.Builder(
          name = NameAttributes._string_value(m, "name").map(Name(_)),
          title = NameAttributes._string_value(m, "title").map(I18nTitle(_))
        )
        Consequence.success(b.build())
      case m: Name => Consequence.success(NameAttributes.simple(m))
      case m: String => Consequence.success(NameAttributes.simple(m))
      case _ => Consequence.failValueInvalid(v, XString)
    }
  }
