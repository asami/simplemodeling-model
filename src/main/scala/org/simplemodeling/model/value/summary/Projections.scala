package org.simplemodeling.model.value.summary

import java.time.ZonedDateTime
import org.goldenport.Consequence
import org.goldenport.datatype.Identifier
import org.goldenport.datatype.I18nBrief
import org.goldenport.datatype.I18nLabel
import org.goldenport.datatype.I18nSummary
import org.goldenport.datatype.I18nTitle
import org.goldenport.datatype.Name
import org.goldenport.datatype.ObjectId
import org.goldenport.datatype.Slug
import org.goldenport.convert.ValueReader
import org.simplemodeling.model.statemachine.Aliveness
import org.simplemodeling.model.statemachine.PostStatus
import org.simplemodeling.model.value.*

/*
 * @since   Apr.  2, 2026
 *  version Apr.  2, 2026
 * @version Apr.  3, 2026
 * @author  ASAMI, Tomoharu
 */
type NameAttributes = org.simplemodeling.model.value.NameAttributes
given ValueReader[NameAttributes] =
  summon[ValueReader[org.simplemodeling.model.value.NameAttributes]].asInstanceOf[ValueReader[NameAttributes]]

object NameAttributes {
  export org.simplemodeling.model.value.NameAttributes.{Holder, BareHolder, Builder}

  def parse(p: Any): Consequence[NameAttributes] =
    summon[ValueReader[org.simplemodeling.model.value.NameAttributes]].readC(p)

  def simple(name: String): NameAttributes =
    org.simplemodeling.model.value.NameAttributes.simple(name)

  def simple(name: Name): NameAttributes =
    org.simplemodeling.model.value.NameAttributes.simple(name)

  def apply(
    name: Name,
    label: Option[I18nLabel],
    title: Option[I18nTitle],
    code: Option[Identifier],
    alias: Option[cats.data.NonEmptyVector[I18nLabel]],
    slug: Option[Slug],
    shortid: Option[Identifier]
  ): org.simplemodeling.model.value.NameAttributes =
    org.goldenport.value.NameAttributes(
      name = name,
      label = label,
      title = title,
      code = code,
      alias = None,
      slug = slug,
      shortid = shortid
    )

  def apply(p: org.simplemodeling.model.value.NameAttributes): org.simplemodeling.model.value.NameAttributes =
    apply(p.name, p.label, p.title, p.code, p.alias, p.slug, p.shortid)
}

type DescriptiveAttributes = org.simplemodeling.model.value.DescriptiveAttributes
given ValueReader[DescriptiveAttributes] =
  summon[ValueReader[org.simplemodeling.model.value.DescriptiveAttributes]].asInstanceOf[ValueReader[DescriptiveAttributes]]

object DescriptiveAttributes {
  export org.simplemodeling.model.value.DescriptiveAttributes.{Holder, BareHolder, Builder, empty}

  def parse(p: Any): Consequence[DescriptiveAttributes] =
    summon[ValueReader[org.simplemodeling.model.value.DescriptiveAttributes]].readC(p)

  def apply(p: org.simplemodeling.model.value.DescriptiveAttributes): org.simplemodeling.model.value.DescriptiveAttributes =
    org.goldenport.value.DescriptiveAttributes(
      headline = p.headline,
      brief = p.brief,
      summary = p.summary,
      description = None,
      lead = None,
      content = None,
      `abstract` = None,
      remarks = None,
      tooltip = p.tooltip
    )
}

type LifecycleAttributes = org.simplemodeling.model.value.LifecycleAttributes
given ValueReader[LifecycleAttributes] =
  summon[ValueReader[org.simplemodeling.model.value.LifecycleAttributes]].asInstanceOf[ValueReader[LifecycleAttributes]]

object LifecycleAttributes {
  export org.simplemodeling.model.value.LifecycleAttributes.Holder

  def parse(p: Any): Consequence[LifecycleAttributes] =
    summon[ValueReader[org.simplemodeling.model.value.LifecycleAttributes]].readC(p)

  def apply(p: org.simplemodeling.model.value.LifecycleAttributes): org.simplemodeling.model.value.LifecycleAttributes =
    org.simplemodeling.model.value.LifecycleAttributes(
      createdAt = p.createdAt,
      updatedAt = p.updatedAt,
      createdBy = Identifier("system"),
      updatedBy = None,
      postStatus = p.postStatus,
      aliveness = p.aliveness
    )
}

type PublicationAttributes = org.simplemodeling.model.value.PublicationAttributes
given ValueReader[PublicationAttributes] =
  summon[ValueReader[org.simplemodeling.model.value.PublicationAttributes]].asInstanceOf[ValueReader[PublicationAttributes]]

object PublicationAttributes {
  export org.simplemodeling.model.value.PublicationAttributes.Holder

  def parse(p: Any): Consequence[PublicationAttributes] =
    summon[ValueReader[org.simplemodeling.model.value.PublicationAttributes]].readC(p)

  def apply(p: org.simplemodeling.model.value.PublicationAttributes): org.simplemodeling.model.value.PublicationAttributes = p
}

type SecurityAttributes = org.simplemodeling.model.value.SecurityAttributes
given ValueReader[SecurityAttributes] =
  summon[ValueReader[org.simplemodeling.model.value.SecurityAttributes]].asInstanceOf[ValueReader[SecurityAttributes]]

object SecurityAttributes {
  export org.simplemodeling.model.value.SecurityAttributes.{Holder, Rights}

  def parse(p: Any): Consequence[SecurityAttributes] =
    summon[ValueReader[org.simplemodeling.model.value.SecurityAttributes]].readC(p)

  def apply(p: org.simplemodeling.model.value.SecurityAttributes): org.simplemodeling.model.value.SecurityAttributes = p
}

type ResourceAttributes = org.simplemodeling.model.value.ResourceAttributes
given ValueReader[ResourceAttributes] =
  summon[ValueReader[org.simplemodeling.model.value.ResourceAttributes]].asInstanceOf[ValueReader[ResourceAttributes]]

object ResourceAttributes {
  export org.simplemodeling.model.value.ResourceAttributes.Holder

  def parse(p: Any): Consequence[ResourceAttributes] =
    summon[ValueReader[org.simplemodeling.model.value.ResourceAttributes]].readC(p)

  def apply(p: org.simplemodeling.model.value.ResourceAttributes): org.simplemodeling.model.value.ResourceAttributes = p
}

type AuditAttributes = org.simplemodeling.model.value.AuditAttributes

object AuditAttributes {
  export org.simplemodeling.model.value.AuditAttributes.Holder

  given ValueReader[AuditAttributes] = summon[ValueReader[org.simplemodeling.model.value.AuditAttributes]].asInstanceOf[ValueReader[AuditAttributes]]

  def parse(p: Any): Consequence[AuditAttributes] =
    summon[ValueReader[org.simplemodeling.model.value.AuditAttributes]].readC(p)

  def apply(p: org.simplemodeling.model.value.AuditAttributes): org.simplemodeling.model.value.AuditAttributes = p
}

type MediaAttributes = org.simplemodeling.model.value.MediaAttributes

object MediaAttributes {
  export org.simplemodeling.model.value.MediaAttributes.Holder

  given ValueReader[MediaAttributes] = summon[ValueReader[org.simplemodeling.model.value.MediaAttributes]].asInstanceOf[ValueReader[MediaAttributes]]

  def parse(p: Any): Consequence[MediaAttributes] =
    summon[ValueReader[org.simplemodeling.model.value.MediaAttributes]].readC(p)

  def apply(p: org.simplemodeling.model.value.MediaAttributes): org.simplemodeling.model.value.MediaAttributes =
    org.simplemodeling.model.value.MediaAttributes(
      url = p.url,
      images = p.images,
      audios = Vector.empty,
      videos = Vector.empty,
      atathments = Vector.empty
    )
}

type ContextualAttributes = org.simplemodeling.model.value.ContextualAttributes

object ContextualAttributes {
  export org.simplemodeling.model.value.ContextualAttributes.Holder

  given ValueReader[ContextualAttributes] = summon[ValueReader[org.simplemodeling.model.value.ContextualAttributes]].asInstanceOf[ValueReader[ContextualAttributes]]

  def parse(p: Any): Consequence[ContextualAttributes] =
    summon[ValueReader[org.simplemodeling.model.value.ContextualAttributes]].readC(p)

  def apply(p: org.simplemodeling.model.value.ContextualAttributes): org.simplemodeling.model.value.ContextualAttributes = p
}

type BaseContent = org.simplemodeling.model.value.BaseContent

object BaseContent {
  export org.simplemodeling.model.value.BaseContent.{Holder, BareHolder, Builder, simple}

  given ValueReader[BaseContent] = summon[ValueReader[org.simplemodeling.model.value.BaseContent]].asInstanceOf[ValueReader[BaseContent]]

  def parse(p: Any): Consequence[BaseContent] =
    summon[ValueReader[org.simplemodeling.model.value.BaseContent]].readC(p)

  def apply(p: org.simplemodeling.model.value.BaseContent): org.simplemodeling.model.value.BaseContent =
    org.goldenport.value.BaseContent(
      nameAttributes = NameAttributes(p.nameAttributes),
      descriptiveAttributes = DescriptiveAttributes(p.descriptiveAttributes)
    )
}
