package org.simplemodeling.model.value

import java.nio.charset.{Charset, StandardCharsets}
import org.goldenport.Consequence
import org.goldenport.convert.ValueReader
import org.goldenport.datatype.{I18nText, MimeType}
import org.goldenport.record.Record
import org.goldenport.schema.XString

/*
 * SimpleEntity content body and derived content-reference index.
 *
 * @since   May.  3, 2026
 * @version May.  4, 2026
 * @author  ASAMI, Tomoharu
 */
type ContentBody = org.goldenport.value.ContentBody

object ContentBody {
  def apply(value: String): ContentBody =
    org.goldenport.value.ContentBody(value)

  given ValueReader[ContentBody] =
    new ValueReader[ContentBody] {
      def readC(v: Any): Consequence[ContentBody] = v match {
        case m: org.goldenport.value.ContentBody => Consequence.success(m)
        case m: I18nText => Consequence.success(ContentBody(m.toI18nString.displayMessage))
        case m: String => Consequence.success(ContentBody(m))
        case _ => Consequence.failValueInvalid(v, XString)
      }
    }
}

type ContentMarkup = org.goldenport.value.ContentMarkup

object ContentMarkup {
  val HtmlFragment: ContentMarkup = org.goldenport.value.ContentMarkup.HtmlFragment
  val MarkdownGfm: ContentMarkup = org.goldenport.value.ContentMarkup.MarkdownGfm
  val SmartDox: ContentMarkup = org.goldenport.value.ContentMarkup.SmartDox

  def parseC(value: String): Consequence[ContentMarkup] =
    org.goldenport.value.ContentMarkup.parseC(value)

  def parseOption(value: String): Option[ContentMarkup] =
    org.goldenport.value.ContentMarkup.parseOption(value)

  given ValueReader[ContentMarkup] =
    new ValueReader[ContentMarkup] {
      def readC(v: Any): Consequence[ContentMarkup] = v match {
        case m: org.goldenport.value.ContentMarkup => Consequence.success(m)
        case m: String => parseC(m)
        case _ => Consequence.failValueInvalid(v, XString)
      }
    }
}

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
    content: Option[ContentBody] = None,
    mimeType: Option[MimeType] = None,
    charset: Option[Charset] = None,
    markup: Option[ContentMarkup] = None,
    references: Vector[ContentReferenceOccurrence] = Vector.empty
  ): ContentAttributes =
    org.goldenport.value.ContentAttributes(
      content = content,
      mimeType = mimeType,
      charset = charset,
      markup = markup,
      references = references
    )

  def empty: ContentAttributes =
    org.goldenport.value.ContentAttributes.empty

  final case class Builder(
    contentAttributes: Option[ContentAttributes] = None,
    content: Option[ContentBody] = None,
    mimeType: Option[MimeType] = None,
    charset: Option[Charset] = None,
    markup: Option[ContentMarkup] = None,
    references: Option[Vector[ContentReferenceOccurrence]] = None
  ) {
    def withContentAttributes(p: ContentAttributes): Builder = copy(contentAttributes = Some(p))
    def withContent(p: ContentBody): Builder = copy(content = Some(p))
    def withContent(p: I18nText): Builder = withContent(ContentBody(p.toI18nString.displayMessage))
    def withContent(p: String): Builder = withContent(ContentBody(p))
    def withMimeType(p: MimeType): Builder = copy(mimeType = Some(p))
    def withMimeType(p: String): Builder = withMimeType(MimeType(p))
    def withContentType(p: String): Builder = withMimeType(p)
    def withCharset(p: Charset): Builder = copy(charset = Some(p))
    def withCharset(p: String): Builder = copy(charset = Some(Charset.forName(p)))
    def withMarkup(p: ContentMarkup): Builder = copy(markup = Some(p))
    def withMarkup(p: String): Builder = copy(markup = ContentMarkup.parseOption(p))
    def withReferences(p: Vector[ContentReferenceOccurrence]): Builder = copy(references = Some(p))

    def build(): ContentAttributes = {
      val base = contentAttributes.getOrElse(empty)
      base.copy(
        content = content.orElse(base.content),
        mimeType = mimeType.orElse(base.mimeType),
        charset = charset.orElse(base.charset),
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
    def contentCharset = contentAttributes.charset
    def contentMarkup = contentAttributes.markup
    def contentReferences = contentAttributes.references
  }

  given ValueReader[ContentAttributes] =
    new ValueReader[ContentAttributes] {
      def readC(v: Any): Consequence[ContentAttributes] = v match {
        case m: ContentAttributes => Consequence.success(m)
        case m: Record => createC(m)
        case s: String => Consequence.success(ContentAttributes(content = Some(ContentBody(s))))
        case _ => Consequence.failValueInvalid(v, XString)
      }
    }

  def createC(record: Record): Consequence[ContentAttributes] =
    org.goldenport.value.ContentAttributes.createC(record)
}
