package org.simplemodeling.model.value.internal

import org.goldenport.Consequence
import org.goldenport.convert.ValueReader
import org.goldenport.schema.XString
import org.simplemodeling.model.value.{given, *}

/*
 * @since   Apr. 21, 2026
 * @version May.  3, 2026
 * @author  ASAMI, Tomoharu
 */
object ProjectionValueReaders {
  val nameAttributes: ValueReader[org.simplemodeling.model.value.NameAttributes] =
    summon[ValueReader[org.simplemodeling.model.value.NameAttributes]]

  val descriptiveAttributes: ValueReader[org.simplemodeling.model.value.DescriptiveAttributes] =
    summon[ValueReader[org.simplemodeling.model.value.DescriptiveAttributes]]

  val contentAttributes: ValueReader[org.simplemodeling.model.value.ContentAttributes] =
    summon[ValueReader[org.simplemodeling.model.value.ContentAttributes]]

  val lifecycleAttributes: ValueReader[org.simplemodeling.model.value.LifecycleAttributes] =
    summon[ValueReader[org.simplemodeling.model.value.LifecycleAttributes]]

  val publicationAttributes: ValueReader[org.simplemodeling.model.value.PublicationAttributes] =
    summon[ValueReader[org.simplemodeling.model.value.PublicationAttributes]]

  val securityAttributes: ValueReader[org.simplemodeling.model.value.SecurityAttributes] =
    summon[ValueReader[org.simplemodeling.model.value.SecurityAttributes]]

  val resourceAttributes: ValueReader[org.simplemodeling.model.value.ResourceAttributes] =
    summon[ValueReader[org.simplemodeling.model.value.ResourceAttributes]]

  val auditAttributes: ValueReader[org.simplemodeling.model.value.AuditAttributes] =
    new ValueReader[org.simplemodeling.model.value.AuditAttributes] {
      def readC(v: Any): Consequence[org.simplemodeling.model.value.AuditAttributes] = v match {
        case m: org.simplemodeling.model.value.AuditAttributes => Consequence.success(m)
        case _ => Consequence.failValueInvalid(v, XString)
      }
    }

  val mediaAttributes: ValueReader[org.simplemodeling.model.value.MediaAttributes] =
    new ValueReader[org.simplemodeling.model.value.MediaAttributes] {
      def readC(v: Any): Consequence[org.simplemodeling.model.value.MediaAttributes] = v match {
        case m: org.simplemodeling.model.value.MediaAttributes => Consequence.success(m)
        case _ => Consequence.failValueInvalid(v, XString)
      }
    }

  val contextualAttributes: ValueReader[org.simplemodeling.model.value.ContextualAttributes] =
    new ValueReader[org.simplemodeling.model.value.ContextualAttributes] {
      def readC(v: Any): Consequence[org.simplemodeling.model.value.ContextualAttributes] = v match {
        case m: org.simplemodeling.model.value.ContextualAttributes => Consequence.success(m)
        case _ => Consequence.failValueInvalid(v, XString)
      }
    }

  val baseContent: ValueReader[org.simplemodeling.model.value.BaseContent] =
    new ValueReader[org.simplemodeling.model.value.BaseContent] {
      def readC(v: Any): Consequence[org.simplemodeling.model.value.BaseContent] = v match {
        case m: org.simplemodeling.model.value.BaseContent => Consequence.success(m)
        case _ => Consequence.failValueInvalid(v, XString)
      }
    }
}
