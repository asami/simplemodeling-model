# Model value conversion

`org.simplemodeling.model.value.ModelValueEncoder.encodeC(value: Any)` returns
`Consequence[Any]` for external Record construction. This is the normative model
projection contract, paired with
[ModelValueEncoderSpec.scala](../../src/test/scala/org/simplemodeling/model/value/ModelValueEncoderSpec.scala).
Current adoption status — 2026-10-18: the selected model conversion repair
passed 54/54 in ten suites. Forced regeneration subsequently compiled the actual
11 model products and inspected their thin encoder delegates, with the same
54/54 coverage; these runs overlap. The connected Core/model/Modeler/CNCF Step
is focused-validated and passed independent complete protected focused
RCP-CONSUMERS Step review.
Phase 115 adoption is CLOSED and all five RCP obligations are DONE. Both
connected Steps were independently reviewed and committed locally. The sole
original Phase full review, retained Modeler repair1 focused closure and
combined repair2–6 focused closure passed. The selected model full gate passed
123 tests in 31 suites, zero failed/canceled/ignored/pending/aborted. The
[Phase acceptance](../../../../dev2025/cloud-native-component-framework/docs/phase/phase-115.md#final-phase-acceptance--2026-10-18)
and [checklist](../../../../dev2025/cloud-native-component-framework/docs/phase/phase-115-checklist.md)
record all four selected full gates and retained failed history. Closure takes
effect through the local release commit containing `phase115-final-closure-20261018-01@1`.

The current selected presentation repair passed 56/56 in ten suites: 26 model
conversion cases, including two added precedence scenarios, plus the original
nine suites with 30 cases. After the tested model 0.2.2-SNAPSHOT local refresh
succeeded, actual generated consumer integration passed 44/44 in five suites.
The [CNCF Knowledge conversion specification](../../../../dev2025/cloud-native-component-framework/src/test/scala/org/goldenport/cncf/knowledge/Phase115KnowledgeExternalConversionSpec.scala)
requires actual generated Information external and stored status to be selected
strings. These runs overlap the historical 54/54 and forced-regeneration evidence
and are not summed. Final Phase acceptance is recorded above.
HYG-CNCF-P115-001 and HYG-CNCF-P115-002 remain OPEN and nonblocking in their
separate journal; no successor is activated.

## Responsibility

The public method MUST delegate directly to Core
`ExternalValueEncoder.encodeC(value, _model_adapter)`. Core owns scalar values,
raw Json, normalized Record values, standard presentation, options and collection
traversal. The model adapter MUST own only the explicit projections below. It
MUST NOT introduce a second generic normalizer, collection traversal, registry,
reflection, arbitrary `toString` fallback, SQL conversion or native null sentinel.

Core-owned NameAttributes, DescriptiveAttributes, ContentAttributes and
ContentReferenceOccurrence MUST retain their existing Core contracts. StateMachine
and Powertype MUST retain their explicit Presentable.print projection through
Core. The model adapter selects these existing scalar meanings before a competing
generic RecordPresentable fallback; NominalScalar remains first. Database
`dbValue` remains the separate storage owner's projection. This selection
explains the existing required print semantics and introduces no new contract.
Exceptions from print callbacks remain Consequence failures, and null prints
fail; neither permits Record fallback. Charset, Locale and TimeZone also remain
Core responsibilities.

## Ordered model projections

The following field sequences are normative external names and order. Fields
MUST be recursively encoded by the same public model encoder, aggregated with
`Consequence.zipN`, and supplied as ordered named values to `Record.dataAuto`.
They MUST NOT pass through Map. Some/None MUST reach ordinary Record construction
so that construction owns optional-field omission.

| Model value | External projection, in field order |
| --- | --- |
| NominalScalar, including EntityRevision and generated nominal datatypes | Recursively encode the underlying `value`, before generic RecordPresentable fallback; no synthetic value Record or wrapper text |
| Condition.Any | None |
| Condition.Is | Recursively encode `expected` |
| Condition.In | Recursively encode `candidates.toVector`; no sorted-order promise |
| Update.Noop, Update.SetNull | Preserve the same typed marker |
| Update.SetValue | Recursively encode `value` and retain Update.SetValue |
| LifecycleAttributes | createdAt, updatedAt, createdBy, updatedBy, postStatus, aliveness |
| PublicationAttributes | publishAt, publicAt, closeAt, startAt, endAt |
| SecurityAttributes | ownerId, groupId, rights, privilegeId |
| SecurityAttributes.Rights | owner, group, other |
| SecurityAttributes.Rights.Permissions | read, write, execute, with exact Boolean values |
| ResourceAttributes | activatedAt, deactivatedAt, expiresAt, activationStatus |
| AuditAttributes, ContextualAttributes | Record.empty |
| MediaAttributes | url, images, audios, videos, attachments |
| SimpleObjectContent | nameAttributes, descriptiveAttributes, contentAttributes, lifecycleAttributes, publicationAttributes, securityAttributes, resourceAttributes, auditAttributes, mediaAttributes, contextualAttribute |
| Image, Audio, Video, Attachment | id, simpleobject |
| Instant, LocalDate, ZonedDateTime | Explicit ISO `toString` |
| LocalDateTime, OffsetDateTime | Explicit ISO `toString`, as selected by the existing ZonedDateTime reader contract |
| URL | Explicit `toExternalForm` |

Security's external camelCase projection is distinct from its snake_case storage
`toRecord`. Media MUST read the existing public `atathments` member while emitting
`attachments`; this contract does not rename that member. No generic
TemporalAccessor or unselected temporal type is admitted.

## Presence, directives and failure

None MUST remain absence and Some MUST retain its wrapper during projection.
Empty collections and empty attribute Records MUST remain present. Ordered native
collections, arrays and Cats NonEmptyVector MUST retain child order and duplicates
through Core. Absent or native-null sequence positions MUST fail the whole
projection rather than be filtered, shifted, or turned into successful empties.
NonEmptyVector MUST therefore never become empty. Set projection promises
membership only. String-keyed maps remain keyed associations; unsupported keys
MUST fail without invoking key.toString.

Raw Json nulls, including object members and array positions, MUST remain raw Json
values. Normalized Record values MUST retain their ownership without recursive
reinterpretation. Exact numbers MUST keep precision and runtime numeric types;
nominal wrappers MUST not alter those scalar semantics.

Projection MUST preserve directive intent without executing it. A subsequently
constructed encoded patch passed to `Update.toChangesRecord` MUST omit Noop,
retain SetNull as a typed clear marker, and unwrap SetValue to its exact projected
value. Required SetValue(null) MUST fail through Core; it MUST NOT become an
implicit clear. Condition projection does not change Condition acceptance or
validation semantics. Unsupported conditions, unknown objects and unsupported
children MUST fail as Consequence values without unknown-object text callbacks.
Independent model field failures MUST aggregate through Consequence.zipN; no
string-only failure helper or successful fallback may replace them.

The executable specification requires at least 100 successful connected model
projection law samples with zero discarded samples, in addition to the explicit
projection, presence, directive and failure scenarios.
