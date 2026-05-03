package org.simplemodeling.model.value

/*
 * @since   Aug.  1, 2025
 *  version Aug.  2, 2025
 *  version Mar. 29, 2026
 * @version May.  3, 2026
 *  version Aug.  2, 2025
 * @author  ASAMI, Tomoharu
 */
case class SimpleObjectContent(
  nameAttributes: NameAttributes,
  descriptiveAttributes: DescriptiveAttributes,
  contentAttributes: ContentAttributes,
  lifecycleAttributes: LifecycleAttributes,
  publicationAttributes: PublicationAttributes,
  securityAttributes: SecurityAttributes,
  resourceAttributes: ResourceAttributes,
  auditAttributes: AuditAttributes,
  mediaAttributes: MediaAttributes,
  contextualAttribute: ContextualAttributes
) {
}

object SimpleObjectContent {
  trait Holder {
    protected def simple_Object_Content: SimpleObjectContent

    def nameAttributes: NameAttributes = simple_Object_Content.nameAttributes
    def descriptiveAttributes: DescriptiveAttributes = simple_Object_Content.descriptiveAttributes
    def contentAttributes: ContentAttributes = simple_Object_Content.contentAttributes
    def lifecycleAttributes: LifecycleAttributes = simple_Object_Content.lifecycleAttributes
    def publicationAttributes: PublicationAttributes = simple_Object_Content.publicationAttributes
    def securityAttributes: SecurityAttributes = simple_Object_Content.securityAttributes
    def resourceAttributes: ResourceAttributes = simple_Object_Content.resourceAttributes
    def auditAttributes: AuditAttributes = simple_Object_Content.auditAttributes
    def mediaAttributes: MediaAttributes = simple_Object_Content.mediaAttributes
    def contextualAttribute: ContextualAttributes = simple_Object_Content.contextualAttribute
  }
}
