package org.simplemodeling.model.value.detail

import org.goldenport.Consequence
import org.goldenport.datatype.Identifier
import org.goldenport.datatype.I18nLabel
import org.goldenport.datatype.I18nTitle
import org.goldenport.datatype.Name
import org.goldenport.datatype.Slug
import org.goldenport.convert.ValueReader
import org.simplemodeling.model.value.internal.ProjectionValueReaders
import org.simplemodeling.model.value.*

/*
 * @since   Apr.  2, 2026
 *  version Apr. 20, 2026
 * @version Apr. 25, 2026
 * @author  ASAMI, Tomoharu
 */
type NameAttributes = org.simplemodeling.model.value.NameAttributes
given ValueReader[NameAttributes] =
  ProjectionValueReaders.nameAttributes.asInstanceOf[ValueReader[NameAttributes]]

object NameAttributes {
  export org.simplemodeling.model.value.NameAttributes.{Holder, BareHolder, Builder}

  def parse(p: Any): Consequence[NameAttributes] =
    ProjectionValueReaders.nameAttributes.readC(p)

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
    slug: Option[Slug]
  ): org.simplemodeling.model.value.NameAttributes =
    org.goldenport.value.NameAttributes(
      name = name,
      label = label,
      title = title,
      code = code,
      alias = alias,
      slug = slug
    )

  def apply(p: org.simplemodeling.model.value.NameAttributes): org.simplemodeling.model.value.NameAttributes =
    apply(p.name, p.label, p.title, p.code, p.alias, p.slug)
}

type DescriptiveAttributes = org.simplemodeling.model.value.DescriptiveAttributes
given ValueReader[DescriptiveAttributes] =
  ProjectionValueReaders.descriptiveAttributes.asInstanceOf[ValueReader[DescriptiveAttributes]]

object DescriptiveAttributes {
  export org.simplemodeling.model.value.DescriptiveAttributes.{Holder, BareHolder, Builder, empty}

  def parse(p: Any): Consequence[DescriptiveAttributes] =
    ProjectionValueReaders.descriptiveAttributes.readC(p)

  def apply(p: org.simplemodeling.model.value.DescriptiveAttributes): org.simplemodeling.model.value.DescriptiveAttributes = p
}

type LifecycleAttributes = org.simplemodeling.model.value.LifecycleAttributes
given ValueReader[LifecycleAttributes] =
  ProjectionValueReaders.lifecycleAttributes.asInstanceOf[ValueReader[LifecycleAttributes]]

object LifecycleAttributes {
  export org.simplemodeling.model.value.LifecycleAttributes.Holder

  def parse(p: Any): Consequence[LifecycleAttributes] =
    ProjectionValueReaders.lifecycleAttributes.readC(p)

  def apply(p: org.simplemodeling.model.value.LifecycleAttributes): org.simplemodeling.model.value.LifecycleAttributes = p
}

type PublicationAttributes = org.simplemodeling.model.value.PublicationAttributes
given ValueReader[PublicationAttributes] =
  ProjectionValueReaders.publicationAttributes.asInstanceOf[ValueReader[PublicationAttributes]]

object PublicationAttributes {
  export org.simplemodeling.model.value.PublicationAttributes.Holder

  def parse(p: Any): Consequence[PublicationAttributes] =
    ProjectionValueReaders.publicationAttributes.readC(p)

  def apply(p: org.simplemodeling.model.value.PublicationAttributes): org.simplemodeling.model.value.PublicationAttributes = p
}

type SecurityAttributes = org.simplemodeling.model.value.SecurityAttributes
given ValueReader[SecurityAttributes] =
  ProjectionValueReaders.securityAttributes.asInstanceOf[ValueReader[SecurityAttributes]]

object SecurityAttributes {
  export org.simplemodeling.model.value.SecurityAttributes.{Holder, Rights}

  def parse(p: Any): Consequence[SecurityAttributes] =
    ProjectionValueReaders.securityAttributes.readC(p)

  def apply(p: org.simplemodeling.model.value.SecurityAttributes): org.simplemodeling.model.value.SecurityAttributes = p
}

type ResourceAttributes = org.simplemodeling.model.value.ResourceAttributes
given ValueReader[ResourceAttributes] =
  ProjectionValueReaders.resourceAttributes.asInstanceOf[ValueReader[ResourceAttributes]]

object ResourceAttributes {
  export org.simplemodeling.model.value.ResourceAttributes.Holder

  def parse(p: Any): Consequence[ResourceAttributes] =
    ProjectionValueReaders.resourceAttributes.readC(p)

  def apply(p: org.simplemodeling.model.value.ResourceAttributes): org.simplemodeling.model.value.ResourceAttributes = p
}

type AuditAttributes = org.simplemodeling.model.value.AuditAttributes

object AuditAttributes {
  export org.simplemodeling.model.value.AuditAttributes.Holder

  given ValueReader[AuditAttributes] = ProjectionValueReaders.auditAttributes.asInstanceOf[ValueReader[AuditAttributes]]

  def parse(p: Any): Consequence[AuditAttributes] =
    ProjectionValueReaders.auditAttributes.readC(p)

  def apply(p: org.simplemodeling.model.value.AuditAttributes): org.simplemodeling.model.value.AuditAttributes = p
}

type MediaAttributes = org.simplemodeling.model.value.MediaAttributes

object MediaAttributes {
  export org.simplemodeling.model.value.MediaAttributes.Holder

  given ValueReader[MediaAttributes] = ProjectionValueReaders.mediaAttributes.asInstanceOf[ValueReader[MediaAttributes]]

  def parse(p: Any): Consequence[MediaAttributes] =
    ProjectionValueReaders.mediaAttributes.readC(p)

  def apply(p: org.simplemodeling.model.value.MediaAttributes): org.simplemodeling.model.value.MediaAttributes = p
}

type ContextualAttributes = org.simplemodeling.model.value.ContextualAttributes

object ContextualAttributes {
  export org.simplemodeling.model.value.ContextualAttributes.Holder

  given ValueReader[ContextualAttributes] = ProjectionValueReaders.contextualAttributes.asInstanceOf[ValueReader[ContextualAttributes]]

  def parse(p: Any): Consequence[ContextualAttributes] =
    ProjectionValueReaders.contextualAttributes.readC(p)

  def apply(p: org.simplemodeling.model.value.ContextualAttributes): org.simplemodeling.model.value.ContextualAttributes = p
}

type BaseContent = org.simplemodeling.model.value.BaseContent

object BaseContent {
  export org.simplemodeling.model.value.BaseContent.{Holder, BareHolder, Builder, simple}

  given ValueReader[BaseContent] = ProjectionValueReaders.baseContent.asInstanceOf[ValueReader[BaseContent]]

  def parse(p: Any): Consequence[BaseContent] =
    ProjectionValueReaders.baseContent.readC(p)

  def apply(p: org.simplemodeling.model.value.BaseContent): org.simplemodeling.model.value.BaseContent =
    org.goldenport.value.BaseContent(
      nameAttributes = NameAttributes(p.nameAttributes),
      descriptiveAttributes = DescriptiveAttributes(p.descriptiveAttributes)
    )
}
