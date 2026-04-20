package org.simplemodeling.model

import org.goldenport.datatype.Identifier
import org.simplemodeling.model.datatype.EntityId
import org.simplemodeling.model.directive.Condition

/*
 * @since   Mar. 23, 2026
 *  version Mar. 29, 2026
 * @version Apr. 20, 2026
 * @author  ASAMI, Tomoharu
 */
abstract class SimpleEntityQuery extends SimpleObjectQuery {
  def id: Condition[EntityId] = Condition.any[EntityId]
  def shortid: Condition[Identifier] = Condition.any[Identifier]
}
