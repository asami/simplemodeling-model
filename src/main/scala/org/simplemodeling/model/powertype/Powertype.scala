package org.simplemodeling.model.powertype

import org.goldenport.text.Presentable

/*
 * @since   Apr.  3, 2026
 * @version Apr.  3, 2026
 * @author  ASAMI, Tomoharu
 */
trait Powertype extends Presentable {
  def value: String
  def dbValue: Option[Int] =
    _invoke_option_int("dbValueOf", value)

  def label: String =
    _invoke_string("labelOf", value).getOrElse(value)

  def print: String = value

  private def _invoke_option_int(method: String, arg: String): Option[Int] =
    _companion.flatMap { companion =>
      try
        companion.getClass.getMethod(method, classOf[String]).invoke(companion, arg).asInstanceOf[Option[Int]]
      catch
        case _: NoSuchMethodException => None
    }

  private def _invoke_string(method: String, arg: String): Option[String] =
    _companion.flatMap { companion =>
      try
        Option(companion.getClass.getMethod(method, classOf[String]).invoke(companion, arg).asInstanceOf[String])
      catch
        case _: NoSuchMethodException => None
    }

  private def _companion: Option[AnyRef] =
    try
      val moduleclass = Class.forName(this.getClass.getName + "$")
      Option(moduleclass.getField("MODULE$").get(null).asInstanceOf[AnyRef])
    catch
      case _: Throwable => None
}
