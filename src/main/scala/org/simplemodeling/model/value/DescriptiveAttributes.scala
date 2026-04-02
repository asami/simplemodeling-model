package org.simplemodeling.model.value

/*
 * @since   Aug.  1, 2025
 *  version Aug.  2, 2025
 *  version Oct.  7, 2025
 *  version Dec. 30, 2025
 *  version Jan. 21, 2026
 *  version Mar. 29, 2026
 * @version Apr.  2, 2026
 *  version Aug.  2, 2025
 *  version Oct.  7, 2025
 *  version Dec. 30, 2025
 *  version Jan. 21, 2026
 * @author  ASAMI, Tomoharu
 */
type DescriptiveAttributes = org.goldenport.value.DescriptiveAttributes

object DescriptiveAttributes {
  export org.goldenport.value.DescriptiveAttributes.BareHolder

  def empty: DescriptiveAttributes =
    org.goldenport.value.DescriptiveAttributes.empty

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
