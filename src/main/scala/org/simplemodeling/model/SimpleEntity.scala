package org.simplemodeling.model

import java.util.Locale
import org.simplemodeling.model.datatype.EntityId

/*
 * @since   Aug.  4, 2025
 *  version Mar. 29, 2026
 *  version Apr.  2, 2026
 * @version Apr. 17, 2026
 * @author  ASAMI, Tomoharu
 */
abstract class SimpleEntity extends SimpleObject {
  def id: EntityId

  // Exposes title as a first-class SimpleEntity attribute for model-generated entities.
  def title: String =
    nameAttributes.title.map(_.value.displayMessage).getOrElse("")

  def title(locale: Locale): String =
    nameAttributes.title.map(_.displayMessage(locale)).getOrElse("")

  def content(locale: Locale): Option[String] =
    content.map(_.displayMessage(locale))
}
