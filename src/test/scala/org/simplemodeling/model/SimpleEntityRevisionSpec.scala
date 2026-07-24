package org.simplemodeling.model

import java.time.Instant
import org.goldenport.datatype.Identifier
import org.simplemodeling.model.datatype.{EntityCollectionId, EntityId, EntityRevision}
import org.simplemodeling.model.statemachine.{Aliveness, PostStatus}
import org.simplemodeling.model.value.*
import org.scalacheck.Gen
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

/*
 * @since   Jul. 25, 2026
 * @version Jul. 25, 2026
 * @author  ASAMI, Tomoharu
 */
final class SimpleEntityRevisionSpec extends AnyWordSpec
    with GivenWhenThen
    with Matchers
    with ScalaCheckDrivenPropertyChecks {
  private val _valid_revision = Gen.chooseNum(EntityRevision.MINIMUM, EntityRevision.MAXIMUM)
  private val _lifecycle_timestamp_pair =
    for {
      createdseconds <- Gen.chooseNum(0L, 4102444800L)
      elapsedseconds <- Gen.chooseNum(0L, 31536000L)
    } yield (
      Instant.ofEpochSecond(createdseconds),
      Instant.ofEpochSecond(createdseconds + elapsedseconds)
    )

  "SimpleEntity revision model" should {
    "expose revision as one standard readable Entity attribute" in {
      Given("a SimpleEntity carrying a framework-managed revision")
      val revision = EntityRevision.createC(7L).toOption.get
      val entity = _entity(revision)

      When("application code reads the Entity")
      val result = entity.revision

      Then("the embedded revision is available as a validated value")
      result shouldBe revision

      And("SimpleEntity declares the revision attribute exactly once")
      classOf[SimpleEntity].getDeclaredMethods.count(_.getName == "revision") shouldBe 1
    }

    "keep revision outside application create and update input models" in {
      Given("the standard SimpleEntity application input types")
      val inputtypes = Vector(classOf[SimpleEntityCreate], classOf[SimpleEntityUpdate], classOf[SimpleEntityQuery])

      When("their public model attributes are inspected")
      val revisionattributes = inputtypes.flatMap(_.getMethods.filter(_.getName == "revision"))

      Then("none exposes revision as writable application input")
      revisionattributes shouldBe empty
    }

    "preserve arbitrary revisions and lifecycle timestamps independently" in {
      forAll(_valid_revision, _lifecycle_timestamp_pair) { (revisionvalue, timestamps) =>
        val (createdat, updatedat) = timestamps

        Given("a SimpleEntity with generated lifecycle timestamps and a valid revision")
        val revision = EntityRevision.createC(revisionvalue).toOption.get
        val lifecycle = _lifecycle(createdat, updatedat)
        val entity = _entity(revision, lifecycle)

        When("revision and lifecycle metadata are read")
        val actualrevision = entity.revision
        val actual = entity.lifecycleAttributes

        Then("revision remains independent from both lifecycle timestamps")
        actualrevision.value shouldBe revisionvalue
        actual.createdAt shouldBe createdat
        actual.updatedAt shouldBe updatedat
      }
    }
  }

  private def _entity(
    revision: EntityRevision,
    lifecycle: LifecycleAttributes = _lifecycle(Instant.EPOCH, Instant.EPOCH)
  ): TestEntity =
    TestEntity(
      id = EntityId("test", "entity", EntityCollectionId("test", "entity", "sample")),
      revision = revision,
      nameAttributes = NameAttributes.simple("sample"),
      descriptiveAttributes = DescriptiveAttributes.empty,
      contentAttributes = ContentAttributes.empty,
      lifecycleAttributes = lifecycle,
      publicationAttributes = PublicationAttributes(None, None, None, None, None),
      securityAttributes = SecurityAttributes.ownedBy("system"),
      resourceAttributes = ResourceAttributes(),
      auditAttributes = AuditAttributes(),
      mediaAttributes = MediaAttributes(None, Vector.empty, Vector.empty, Vector.empty, Vector.empty),
      contextualAttribute = ContextualAttributes()
    )

  private def _lifecycle(createdat: Instant, updatedat: Instant): LifecycleAttributes =
    LifecycleAttributes(
      createdAt = createdat,
      updatedAt = updatedat,
      createdBy = Identifier("system"),
      updatedBy = Identifier("system"),
      postStatus = PostStatus.default,
      aliveness = Aliveness.default
    )

  private final case class TestEntity(
    id: EntityId,
    revision: EntityRevision,
    nameAttributes: NameAttributes,
    descriptiveAttributes: DescriptiveAttributes,
    contentAttributes: ContentAttributes,
    lifecycleAttributes: LifecycleAttributes,
    publicationAttributes: PublicationAttributes,
    securityAttributes: SecurityAttributes,
    resourceAttributes: ResourceAttributes,
    auditAttributes: AuditAttributes,
    mediaAttributes: MediaAttributes,
    contextualAttribute: ContextualAttributes
  ) extends SimpleEntity
}
