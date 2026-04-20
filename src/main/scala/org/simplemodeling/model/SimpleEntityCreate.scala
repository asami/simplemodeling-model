package org.simplemodeling.model

import org.goldenport.datatype.Identifier
import org.simplemodeling.model.datatype.EntityId

/*
 * @since   Mar. 23, 2026
 *  version Mar. 29, 2026
 * @version Apr. 20, 2026
 * @author  ASAMI, Tomoharu
 */
abstract class SimpleEntityCreate extends SimpleObjectCreate {
  def id: Option[EntityId] = None
  def shortid: Option[Identifier] = id.map(x => Identifier(x.parts.entropy))
}
