package org.simplemodeling.model.value

/*
 * @since   Aug.  1, 2025
 *  version Aug.  2, 2025
 *  version Dec. 30, 2025
 *  version Jan. 21, 2026
 *  version Mar. 29, 2026
 * @version Apr.  2, 2026
 *  version Aug.  2, 2025
 *  version Dec. 30, 2025
 *  version Jan. 21, 2026
 * @author  ASAMI, Tomoharu
 */
type NameAttributes = org.goldenport.value.NameAttributes

object NameAttributes {
  export org.goldenport.value.NameAttributes.BareHolder

  def simple(name: String): NameAttributes =
    org.goldenport.value.NameAttributes.simple(name)

  def simple(name: org.goldenport.datatype.Name): NameAttributes =
    org.goldenport.value.NameAttributes.simple(name)

  trait Holder {
    def nameAttributes: NameAttributes

    protected def name_Attributes: NameAttributes = nameAttributes

    def name = nameAttributes.name
    def label = nameAttributes.label
  }
}
