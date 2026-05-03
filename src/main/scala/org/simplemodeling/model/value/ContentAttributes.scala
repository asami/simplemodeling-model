package org.simplemodeling.model.value

import org.goldenport.Consequence
import org.goldenport.convert.ValueReader
import org.goldenport.datatype.I18nText
import org.goldenport.record.Record
import org.goldenport.schema.XString

/*
 * SimpleEntity content body and derived content-reference index.
 *
 * @since   May.  3, 2026
 * @version May.  3, 2026
 * @author  ASAMI, Tomoharu
 */
type ContentReferenceOccurrence = org.goldenport.value.ContentReferenceOccurrence

object ContentReferenceOccurrence {
  def apply(
    contentField: Option[String] = None,
    markup: Option[String] = None,
    elementKind: Option[String] = None,
    attributeName: Option[String] = None,
    occurrenceIndex: Int = 0,
    originalRef: Option[String] = None,
    normalizedRef: Option[String] = None,
    referenceKind: Option[String] = None,
    urn: Option[String] = None,
    targetEntityId: Option[String] = None,
    label: Option[String] = None,
    alt: Option[String] = None,
    title: Option[String] = None,
    rel: Option[String] = None,
    mediaType: Option[String] = None,
    sortOrder: Option[Int] = None
  ): ContentReferenceOccurrence =
    org.goldenport.value.ContentReferenceOccurrence(
      contentField = contentField,
      markup = markup,
      elementKind = elementKind,
      attributeName = attributeName,
      occurrenceIndex = occurrenceIndex,
      originalRef = originalRef,
      normalizedRef = normalizedRef,
      referenceKind = referenceKind,
      urn = urn,
      targetEntityId = targetEntityId,
      label = label,
      alt = alt,
      title = title,
      rel = rel,
      mediaType = mediaType,
      sortOrder = sortOrder
    )

  def createC(record: Record): Consequence[ContentReferenceOccurrence] =
    org.goldenport.value.ContentReferenceOccurrence.createC(record)

  given ValueReader[ContentReferenceOccurrence] =
    new ValueReader[ContentReferenceOccurrence] {
      def readC(v: Any): Consequence[ContentReferenceOccurrence] = v match {
        case m: ContentReferenceOccurrence => Consequence.success(m)
        case m: Record => createC(m)
        case _ => Consequence.failValueInvalid(v, XString)
      }
    }
}

type ContentAttributes = org.goldenport.value.ContentAttributes

object ContentAttributes {
  def apply(
    content: Option[I18nText] = None,
    mimeType: Option[String] = None,
    markup: Option[String] = None,
    references: Vector[ContentReferenceOccurrence] = Vector.empty
  ): ContentAttributes =
    org.goldenport.value.ContentAttributes(
      content = content,
      mimeType = mimeType,
      markup = markup,
      references = references
    )

  def empty: ContentAttributes =
    org.goldenport.value.ContentAttributes.empty

  final case class Builder(
    contentAttributes: Option[ContentAttributes] = None,
    content: Option[I18nText] = None,
    mimeType: Option[String] = None,
    markup: Option[String] = None,
    references: Option[Vector[ContentReferenceOccurrence]] = None
  ) {
    def withContentAttributes(p: ContentAttributes): Builder = copy(contentAttributes = Some(p))
    def withContent(p: I18nText): Builder = copy(content = Some(p))
    def withContent(p: String): Builder = copy(content = Some(I18nText(p)))
    def withMimeType(p: String): Builder = copy(mimeType = Some(p))
    def withContentType(p: String): Builder = withMimeType(p)
    def withMarkup(p: String): Builder = copy(markup = Some(p))
    def withReferences(p: Vector[ContentReferenceOccurrence]): Builder = copy(references = Some(p))

    def build(): ContentAttributes = {
      val base = contentAttributes.getOrElse(empty)
      base.copy(
        content = content.orElse(base.content),
        mimeType = mimeType.orElse(base.mimeType),
        markup = markup.orElse(base.markup),
        references = references.getOrElse(base.references)
      )
    }
  }

  object Builder {
    def apply(p: ContentAttributes): Builder =
      Builder(contentAttributes = Some(p))
  }

  trait Holder {
    def contentAttributes: ContentAttributes

    protected def content_Attributes: ContentAttributes = contentAttributes

    def content = contentAttributes.content
    def contentMimeType = contentAttributes.mimeType
    def contentMarkup = contentAttributes.markup
    def contentReferences = contentAttributes.references
  }

  given ValueReader[ContentAttributes] =
    new ValueReader[ContentAttributes] {
      def readC(v: Any): Consequence[ContentAttributes] = v match {
        case m: ContentAttributes => Consequence.success(m)
        case m: Record => createC(m)
        case s: String => Consequence.success(ContentAttributes(content = Some(I18nText(s))))
        case _ => Consequence.failValueInvalid(v, XString)
      }
    }

  def createC(record: Record): Consequence[ContentAttributes] =
    org.goldenport.value.ContentAttributes.createC(record)
}
