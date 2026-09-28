# Hygiene Resolution Batch Handoff

Status: COMPLETE
Validated On: 2026-09-28
Validation Evidence: invocation `model-hygiene-full-c122eb7e1aa04fed`; `sbt --batch clean test`; 97 passed, zero pending, zero warnings; receipt `/tmp/skill.cncf.d/cncf-command-execution-5ee84c5d33777e01ba2c80cdf524610de6487a245aee7c0888570e8d2814f7b6-f79e9e1ae0d6846b6f7f2e6c4c74b280/command-execution-sha256-f756832b848abad48707adc7be4eb9342bb4745c5b516b1d629b5f6bd9ad00ef` SHA-256 `f756832b848abad48707adc7be4eb9342bb4745c5b516b1d629b5f6bd9ad00ef`; lock released.
Created: 2026-09-28
Source Repository: /Users/asami/src/dev2026/simplemodeling-model
Target Repositories: /Users/asami/src/dev2026/simplemodeling-model
Suggested Invocation: $cncf-goal-hygiene /Users/asami/src/dev2026/simplemodeling-model/docs/journal/2026/09/2026-09-28-hygiene-resolution-batch-handoff.md

## Purpose

Resolve the two model-owned observations recorded by the CNCF independence repair:
deprecated failure APIs and 27 placeholder scenarios. Preserve current library
behavior, diagnostic taxonomy, public signatures, generation and dependencies.

## Included Hygiene

| ID | Source | Evidence | Work Package | Required outcome |
| --- | --- | --- | --- | --- |
| HYG-MODEL-20260928-01 | `docs/journal/2026/09/2026-09-28-cncf-independent-library-generation.md` | 71 concrete Compile deprecations in 14 handwritten source files | HP-001 | Use nondeprecated equivalent failure construction without changing errors |
| HYG-MODEL-20260928-02 | `docs/journal/2026/09/2026-09-28-cncf-independent-library-generation.md` | 27 pending scaffold cases in 14 specs | HP-002 | Execute existing delegation, reader and state-value contracts |

Hygiene Triage: HANDED_OFF
Hygiene ID: HYG-MODEL-20260928-01
Hygiene Triage: HANDED_OFF
Hygiene ID: HYG-MODEL-20260928-02

## Frozen Boundary

- Allowed repositories: simplemodeling-model only.
- Preserve paths: None; initial index and worktree are clean.
- Allowed behavior change: none.
- Prohibited expansion: production features, architecture, public signature/schema,
  persistence, security/lifecycle policy, dependency coordinates, generated-source
  patches, upstream changes, publication or push.
- Cozy/JST and CNCF observations in the source paragraph belong to other owners;
  their later repair is historical context and is not admitted here.
- Source journal, this handoff and its package ledger are goal-owned management paths.

## HP-001 — Preserve failures while removing deprecated entry points

- Hygiene IDs: HYG-MODEL-20260928-01
- Repository: /Users/asami/src/dev2026/simplemodeling-model
- Targets:
  - `src/main/scala/org/simplemodeling/model/datatype/EntityId.scala`
  - `src/main/scala/org/simplemodeling/model/statemachine/ActivationStatus.scala`
  - `src/main/scala/org/simplemodeling/model/statemachine/Aliveness.scala`
  - `src/main/scala/org/simplemodeling/model/statemachine/PostStatus.scala`
  - `src/main/scala/org/simplemodeling/model/value/BasicValueReaders.scala`
  - `src/main/scala/org/simplemodeling/model/value/ContentAttributes.scala`
  - `src/main/scala/org/simplemodeling/model/value/DescriptiveAttributes.scala`
  - `src/main/scala/org/simplemodeling/model/value/LifecycleAttributes.scala`
  - `src/main/scala/org/simplemodeling/model/value/NameAttributes.scala`
  - `src/main/scala/org/simplemodeling/model/value/PublicationAttributes.scala`
  - `src/main/scala/org/simplemodeling/model/value/ResourceAttributes.scala`
  - `src/main/scala/org/simplemodeling/model/value/SecurityAttributes.scala`
  - `src/main/scala/org/simplemodeling/model/value/SimpleObjectAttributeUpdate.scala`
  - `src/main/scala/org/simplemodeling/model/value/internal/ProjectionValueReaders.scala`
  - `build.sbt`
  - `src/test/scala/org/simplemodeling/model/datatype/LegacyFailureCompatibilitySpec.scala`

- Allowed repair: replace `failValueInvalid(value, datatype)` with the identical
  `valueInvalid(value, datatype)` overload. Replace deprecated `failure(message)`
  with its exact existing implementation, `Failure(Conclusion.simple(message))`,
  retaining status, taxonomy, message and absence of added source metadata.
  Maintain Scala headers and expose deprecation diagnostics in Compile options.
- Prohibited expansion: change parsing, fallback, error classification/messages or
  replace legacy string failures with a different semantic taxonomy.
- Focused validation: `sbt --batch testOnly org.simplemodeling.model.datatype.EntityIdSpec org.simplemodeling.model.datatype.LegacyFailureCompatibilitySpec org.simplemodeling.model.statemachine.* org.simplemodeling.model.value.ContentBodyBoundarySpec org.simplemodeling.model.value.SecurityAttributesSpec`
- Dependencies: None.
- Package State: FOCUSED_PASS
- Evidence: HP-001 invocation `model-hygiene-hp1-994c532f089e4e9a`; 41 passed, zero warnings; lock released.

## HP-002 — Replace scaffold pending cases with existing-contract specifications

- Hygiene IDs: HYG-MODEL-20260928-02
- Repository: /Users/asami/src/dev2026/simplemodeling-model
- Targets:
  - `src/test/scala/org/simplemodeling/model/SimpleEntitySpec.scala`
  - `src/test/scala/org/simplemodeling/model/SimpleObjectSpec.scala`
  - `src/test/scala/org/simplemodeling/model/statemachine/StateMachineSpec.scala`
  - `src/test/scala/org/simplemodeling/model/value/AttachmentSpec.scala`
  - `src/test/scala/org/simplemodeling/model/value/AudioSpec.scala`
  - `src/test/scala/org/simplemodeling/model/value/AuditAttributesSpec.scala`
  - `src/test/scala/org/simplemodeling/model/value/ContextualAttributesSpec.scala`
  - `src/test/scala/org/simplemodeling/model/value/ImageSpec.scala`
  - `src/test/scala/org/simplemodeling/model/value/LifecycleAttributesSpec.scala`
  - `src/test/scala/org/simplemodeling/model/value/MediaAttributesSpec.scala`
  - `src/test/scala/org/simplemodeling/model/value/PublicationAttributesSpec.scala`
  - `src/test/scala/org/simplemodeling/model/value/ResourceAttributesSpec.scala`
  - `src/test/scala/org/simplemodeling/model/value/SimpleObjectContentSpec.scala`
  - `src/test/scala/org/simplemodeling/model/value/VideoSpec.scala`
  - `src/test/scala/org/simplemodeling/model/ModelHygieneFixtures.scala`

- Allowed repair: specify existing attribute/identity delegation, sparse/default
  reader behavior, malformed-value rejection and state identity. Use meaningful
  property checks where inputs vary, AnyWordSpec, should matchers and adjacent
  Given/When/Then. Preserve every previously executable case.
- Prohibited expansion: invent model invariants, add production features, discard
  actual coverage or hide unfinished contracts by canceling/deleting scenarios.
- Focused validation: `sbt --batch testOnly org.simplemodeling.model.SimpleEntitySpec org.simplemodeling.model.SimpleObjectSpec org.simplemodeling.model.statemachine.StateMachineSpec org.simplemodeling.model.value.AttachmentSpec org.simplemodeling.model.value.AudioSpec org.simplemodeling.model.value.AuditAttributesSpec org.simplemodeling.model.value.ContextualAttributesSpec org.simplemodeling.model.value.ImageSpec org.simplemodeling.model.value.LifecycleAttributesSpec org.simplemodeling.model.value.MediaAttributesSpec org.simplemodeling.model.value.PublicationAttributesSpec org.simplemodeling.model.value.ResourceAttributesSpec org.simplemodeling.model.value.SimpleObjectContentSpec org.simplemodeling.model.value.VideoSpec`
- Dependencies: HP-001.
- Package State: FOCUSED_PASS
- Evidence: HP-002 invocation `model-hygiene-hp2-98f3fd104e2b4d54`; 32 passed, zero pending; lock released.

## Final Focused Review

- Exact target programs/files: union of HP-001, HP-002 and two management journals.
- Required checks: both IDs, whole-target naming/headers/spec compliance, behavioral
  preservation, unchanged diagnostic contract and consumer compatibility, focused
  evidence, no hidden scope expansion.
- Failure policy: stop without commit; no automatic review-fix/re-review loop.

## Final Full-Validation Gate

1. `simplemodeling-model: sbt --batch clean test`

Run once on the reviewed tree, after a CLEAN independent focused review.
Require all tests pass, zero pending scaffolds and zero Compile deprecations.
Stop on failure. Use the serialized registered SBT runner.

## Completion Contract

- Commit only after final review and full validation pass.
- The only predeclared post-gate edits are management fields: source Hygiene
  Status RESOLVED, Resolution Batch, Validated On date, exact final validation
  invocation/receipt, and this batch Status COMPLETE with those same references.
- Requirements, code, tests and evidence interpretation are frozen before review.
- These staged closure states become authoritative only after the grouped local
  acceptance commit succeeds. Report commit identity externally.
- Do not absorb Development Candidates or newly found unrelated Hygiene.

## Non-goals

- Cozy, CNCF, core, sbt-cozy and generated-source modification.
- Artifact publication, dependency upgrades, remote writes or release conversion.
