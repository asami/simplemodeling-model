package org.simplemodeling.model.value

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/*
 * @since   Mar. 25, 2026
 * @version Mar. 29, 2026
 * @author  ASAMI, Tomoharu
 */
class GeneratedAddressBuilderSpec extends AnyFlatSpec with Matchers {
  private def generatedAddressAvailable: Boolean =
    scala.util.Try(Class.forName("domain.value.Address$")).isSuccess &&
      scala.util.Try(Class.forName("domain.value.CountryCode$")).isSuccess

  "generated CountryCode builder" should "validate the pattern at value creation time" in {
    if (!generatedAddressAvailable) cancel("generated address model is not available")

    val module = Class.forName("domain.value.CountryCode$").getField("MODULE$").get(null)
    val apply = module.getClass.getMethod("apply", classOf[String])
    val result = intercept[java.lang.reflect.InvocationTargetException] {
      apply.invoke(module, "jp")
    }

    result.getCause shouldBe a[IllegalArgumentException]
    result.getCause.getMessage should include("value must match")
  }

  "generated Address builder" should "validate required fields at build time" in {
    if (!generatedAddressAvailable) cancel("generated address model is not available")

    val builderClass = Class.forName("domain.value.Address$Builder")
    val builder = builderClass
      .getDeclaredConstructor(
        Class.forName("scala.Option"),
        Class.forName("scala.Option"),
        Class.forName("scala.Option"),
        Class.forName("scala.Option"),
        Class.forName("scala.Option"),
        Class.forName("scala.Option"),
        Class.forName("scala.Option"),
        Class.forName("scala.collection.immutable.Vector")
      )
      .newInstance(
        builderClass.getMethod("$lessinit$greater$default$1").invoke(null),
        builderClass.getMethod("$lessinit$greater$default$2").invoke(null),
        builderClass.getMethod("$lessinit$greater$default$3").invoke(null),
        builderClass.getMethod("$lessinit$greater$default$4").invoke(null),
        builderClass.getMethod("$lessinit$greater$default$5").invoke(null),
        builderClass.getMethod("$lessinit$greater$default$6").invoke(null),
        builderClass.getMethod("$lessinit$greater$default$7").invoke(null),
        builderClass.getMethod("$lessinit$greater$default$8").invoke(null)
      )
    builder.getClass.getMethod("withAddressCountry", classOf[String]).invoke(builder, "JP")
    builder.getClass.getMethod("withPostalCode", classOf[String]).invoke(builder, "160-0022")
    val result = intercept[java.lang.reflect.InvocationTargetException] {
      builder.getClass.getMethod("build").invoke(builder)
    }

    result.getCause.getClass.getName shouldBe "org.goldenport.ConsequenceException"
    result.getCause.getMessage.nonEmpty shouldBe true
  }
}
