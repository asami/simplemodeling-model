package org.simplemodeling.model.value

import org.goldenport.datatype.{I18nBrief, I18nDescription, I18nSummary}
import org.goldenport.convert.ValueReader
import org.goldenport.Consequence
import org.goldenport.record.Record
import org.goldenport.schema.XString

/*
 * @since   Aug.  1, 2025
 *  version Aug.  2, 2025
 *  version Oct.  7, 2025
 *  version Dec. 30, 2025
 *  version Jan. 21, 2026
 *  version Mar. 29, 2026
 *  version Apr.  2, 2026
 * @version Apr.  3, 2026
 * @author  ASAMI, Tomoharu
 */
type DescriptiveAttributes = org.goldenport.value.DescriptiveAttributes

object DescriptiveAttributes {
  export org.goldenport.value.DescriptiveAttributes.BareHolder

  def empty: DescriptiveAttributes =
    org.goldenport.value.DescriptiveAttributes.empty

  final case class Builder(
    descriptiveAttributes: Option[DescriptiveAttributes] = None,
    headline: Option[I18nBrief] = None,
    summary: Option[I18nSummary] = None,
    description: Option[I18nDescription] = None
  ) {
    def withDescriptiveAttributes(p: DescriptiveAttributes): Builder = copy(descriptiveAttributes = Some(p))
    def withHeadline(p: I18nBrief): Builder = copy(headline = Some(p))
    def withHeadline(p: String): Builder = copy(headline = Some(I18nBrief(p)))
    def withSummary(p: I18nSummary): Builder = copy(summary = Some(p))
    def withSummary(p: String): Builder = copy(summary = Some(I18nSummary(p)))
    def withDescription(p: I18nDescription): Builder = copy(description = Some(p))
    def withDescription(p: String): Builder = copy(description = Some(I18nDescription(p)))

    def build(): DescriptiveAttributes = {
      val base = descriptiveAttributes.getOrElse(empty)
      val withheadline = headline.fold(base)(x => base.copy(headline = Some(x)))
      val withsummary = summary.fold(withheadline)(x => withheadline.copy(summary = Some(x)))
      description.fold(withsummary)(x => withsummary.copy(description = Some(x)))
    }
  }

  object Builder {
    def apply(p: DescriptiveAttributes): Builder =
      Builder(descriptiveAttributes = Some(p))
  }

  private[value] def _string_value(p: Record, key: String): Option[String] =
    p.getAny(key).collect { case s: String => s }

  trait Holder {
    def descriptiveAttributes: DescriptiveAttributes

    protected def descriptive_Attributes: DescriptiveAttributes = descriptiveAttributes

    def headline = descriptiveAttributes.headline
    def brief = descriptiveAttributes.brief
    def summary = descriptiveAttributes.summary
    def description = descriptiveAttributes.description
    def lead = descriptiveAttributes.lead
    def content = descriptiveAttributes.content
    def `abstract` = descriptiveAttributes.`abstract`
    def remarks = descriptiveAttributes.remarks
    def tooltip = descriptiveAttributes.tooltip
  }
}

given ValueReader[DescriptiveAttributes] =
  new ValueReader[DescriptiveAttributes] {
    def readC(v: Any): Consequence[DescriptiveAttributes] = v match {
      case m: org.goldenport.value.DescriptiveAttributes => Consequence.success(m)
      case m: Record =>
        val b = DescriptiveAttributes.Builder(
          headline = DescriptiveAttributes._string_value(m, "headline").map(I18nBrief(_)),
          summary = DescriptiveAttributes._string_value(m, "summary").map(I18nSummary(_)),
          description = DescriptiveAttributes._string_value(m, "description").map(I18nDescription(_))
        )
        Consequence.success(b.build())
      case _ => Consequence.failValueInvalid(v, XString)
    }
  }
