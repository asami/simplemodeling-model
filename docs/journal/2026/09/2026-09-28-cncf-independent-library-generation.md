# CNCF-independent library generation

Date: 2026-09-28, Mac mini.

The user requested CNCF independence using the upstream
[Cozy handoff](https://github.com/asami/cozy/blob/main/docs/journal/2026/09/2026-09-28-library-generation-cncf-independence-handoff.md).
The repair boundary is Cozy, sbt-cozy, and simplemodeling-model. The existing
CNCF consumer is included for validation, without source changes.

## Confirmed cause and baseline

The previous native `modeler-scala-value` generation retained a CNCF dependency
because the shared Scala generator emitted an action compiler even when no
runtime definition existed. Removing the dependency failed on its references
to `org.goldenport.cncf.unitofwork.ExecProgram` and `UnitOfWorkOp`.

Both current CML inputs preserve `COMPONENT SimpleModelingModel` and
`PACKAGE org.simplemodeling.model`. Their hashes are:

- `address.cml`: `125fa14aeb284a8a2b1ee9dc9309989c7a4b36661ace1618275d5c032326a256`
- `user-profile-values.cml`: `2cac3391b5b47daf101192761b1a5772ff7eacb957f9a78f6b170b3a53cd0883`

The existing fresh output from these unchanged inputs and the same executing
Cozy 0.3.3-SNAPSHOT contains 29 per-input Scala files: 11 distinct value files
and nine identical runtime helpers repeated between inputs. The combined
output contains 20 files. `LogicalActionCompiler.scala` accounts for all four
CNCF reference occurrences across the two input outputs. Handwritten model
sources and tests contain no CNCF references.

Baseline failure evidence:
`/tmp/skill.cncf.d/cncf-sbt-95e4a3fe55d02cca9cd664febf2efc6a871d73fae5339064b2eba2ef4f0b59e1-f368561ba83f8a22921b2f558c9408b5/51235-20260928T020702Z.log`.
This evidence is reused rather than represented as a new command invocation.

## Frozen repair contract

Library generation has an explicit `library` target. CNCF generation keeps
its existing target, descriptor admission, compatibility checks, runtime
code, and provenance requirements. The library path accepts an exact Cozy
generator version without selecting a CNCF runtime descriptor or version.
Runtime entities, services, workflows, composite state machines, actions,
and other execution contracts must fail explicitly when requested on the
library target; they must not silently lose behavior.

Pure values, datatypes, powertypes, and standalone state-machine types retain
their source meaning. Component/package declarations continue to determine
namespace ownership and do not produce a synthetic runtime component.
No generated-source filtering, patching, stubbing, or declaration removal is
an acceptable substitute for correcting the generation boundary.

The model returns to standard sbt-cozy generation, with a new owned managed
source directory so previous workaround output cannot enter acceptance.
Its CNCF declarations will be removed only after fresh library generation
has demonstrated that the dependency is unnecessary.

Runtime identities remain Cozy 0.3.3-SNAPSHOT, sbt-cozy 0.1.18-SNAPSHOT,
simplemodeling-model 0.2.2-SNAPSHOT, and SimpleModeler 1.1.26-SNAPSHOT. No
release conversion, remote publication, commit, or push is authorized.

Parent-owned workflow/evidence ledger:
`/tmp/skill.cncf.d/sbt-preparation-0c03bc145995bfc53949ab0a40fc60c931817dbaaac0759f726f5e49a8f2de2e-788cf09e52cf3db061d208353ae0c64d/state.json`.

## Implemented boundary

Cozy now carries an explicit `ModelGenerationTarget` through CLI admission,
Modeler, and ScalaGenerator. Library admission runs before Kaleidox evaluation,
so unsupported runtime features produce an exception instead of an SError that
could leave the native command successful. The library transformer retains pure
declarations and skips CNCF support emitters. Its JSON sbt bridge requires the
exact executing Cozy version, canonical ownership, and the library target;
runtime descriptor/version arguments and CAR/SAR library selection are rejected.
The default CNCF branch retains descriptor, compatibility, and provenance checks.

sbt-cozy exposes `cozyGenerationTarget`. Maven libraries default to `library`;
other projects default to `cncf`. An explicit CNCF target remains available for
libraries requiring execution support. Library generation uses
`modeler-scala-value`, requires a Cozy pin and model metadata, and includes the
target in incremental state without extracting a runtime descriptor or requiring
CNCF generation provenance. Supplied runtime keys are rejected even if empty.

The model uses this standard bridge with Cozy 0.3.3-SNAPSHOT. Both its direct
CNCF dependency and project.yaml CNCF declaration were removed. The two CML
files, component/package declarations, Scala version, core dependency, and
model artifact version remain unchanged. Generated source is owned under
`target/library-model/src_managed/main`, isolated from earlier workaround output.

## Verification before consumer refresh

- Cozy: 67 tests in 10 focused suites succeeded, with zero failures, followed
  by successful `cozyExportRuntimeClasspath`. The selection covers actual
  native/JSON library generation and existing CNCF action, workflow, provided
  API, descriptor, and multi-source provenance behavior.
- Native launcher controls: each actual model CML generated successfully using
  `modeler-scala-value <input> --save <owned-output> --generation-target library
  --cozy-generator-version 0.3.3-SNAPSHOT`. The combined output contains 11
  value sources, byte-identical to the corresponding former value sources,
  without CNCF references, synthetic components, action compiler, or bootstrap.
- sbt-cozy: 44 tests in `CozyDelegatedGeneratorSpec`,
  `CozyCncfRuntimeDescriptorSpec`, and
  `CozyGenerationProvenanceIntegrationSpec` succeeded, including the real Cozy
  CAR integration. Its old July fixture required canonical coordinates,
  Scala 3 output paths, ABI v2, and the aggregate-v2 packaging validator.
  Production validation was not weakened. Initial failures and a runner
  locator transcription error are retained in the workflow ledger.
- `sbt --batch publishLocal` successfully refreshed sbt-cozy 0.1.18-SNAPSHOT.
  The model's actual meta-build loads
  `~/.ivy2/local/org.goldenport/sbt-cozy/scala_2.12/sbt_1.0/0.1.18-SNAPSHOT/jars/sbt-cozy.jar`,
  SHA-256 `23ccaa568f3825fa1250282f38ef2d06fad31c3db57dbdd9939532d1046c482c`,
  identical to the newly published plugin JAR.
- Model: `sbt --batch cozyGenerate test 'show Compile / dependencyClasspath'`
  succeeded. All 67 executed tests passed in 29 suites; 27 tests remain
  pending. It freshly generated 11 sources and compiled 59 Scala sources.
  All 62 entries in its resolved Compile dependency classpath are free of
  CNCF artifacts. Generated source and handwritten main/test source contain
  no CNCF package references.
- Recorded generation state has exactly five settings: canonical namespace,
  ID, component version, `generation.target=library`, and exact Cozy version.
  Two model-metadata files are installed; CNCF generation provenance is absent.
  An old `target/sbt-cozy/cncf-runtime.yaml` from 01:22 UTC remains on disk;
  this run neither selects nor refreshes it (fresh generation was 03:26 UTC).
  Previous ignored output was preserved, rather than deleted to demonstrate
  independence.
- The separate Cozy tool runtime contains `cncf-collaborator-api` 0.2.0 but
  no goldenport-cncf runtime JAR. Tool dependencies are distinct from the
  generated library's dependencies.

Successful validation logs:

- Cozy: `/tmp/skill.cncf.d/cncf-sbt-d833f2cfbe2c42d25444b0586a1af4eb953a93f5ebb22490e307c6cbd116d723-b49f0d8f5ca947dde797f7429b18ab87/55174-20260928T030223Z.log`
- sbt-cozy: `/tmp/skill.cncf.d/cncf-sbt-994ff9b9905ddcb956518545a6a951b27919c86d76878946b7ab61def9866f4e-1451d71404a74a0c589bae0a0e711551/57498-20260928T032407Z.log`
- Model: `/tmp/skill.cncf.d/cncf-sbt-8d886543339af77e96b2e2ad4a411087b142fa3617ec835a3f1ebda7f0c3638b-6926a2dfab162164d270d5aff933f006/58065-20260928T032633Z.log`

Detailed generated-file inventory and resolved classpath are recorded in
`model-generation-dependency-inspection.json` beside the parent workflow ledger.
All top-level SBT commands use the registered runner and shared serialized
wrapper, with final `sbt_exit=0 wrapper_exit=0 lock=released`. Python inspection
and protocol helpers use workstation uv-managed Python >=3.12; system Python,
Codex internal runtimes, and shell profiles are not used for this work.

## Published artifact and CNCF consumer acceptance

`sbt --batch publishLocal` successfully refreshed
`org.simplemodeling:simplemodeling-model_3:0.2.2-SNAPSHOT` in the normal local
Ivy repository. The producer JAR and installed JAR have identical SHA-256:
`4cece4655f7cb4f2a7182fef340e73e5616addaa021a6dbbb7c1004b384ffceb`.
All 464 class/TASTy entries were inspected: no CNCF package references and no
former synthetic component/action/workflow runtime helpers remain. Both the
published Maven POM and Ivy dependency definition are CNCF-free.

The unchanged CNCF 0.5.3-SNAPSHOT checkout then ran:

```sh
sbt --batch generateInformationCmlModel compile 'show Compile / dependencyClasspath'
```

Information CML generation and native provenance validation succeeded, followed
by successful incremental compilation of 136 Scala sources. The actual consumer
classpath loads
`~/.ivy2/local/org.simplemodeling/simplemodeling-model_3/0.2.2-SNAPSHOT/jars/simplemodeling-model_3.jar`
with the identical new SHA-256 above. The CNCF worktree remains clean.
Its existing descriptor selection, mutable development pair admission, and
generation provenance checks remain active.

Evidence:

- Model publishLocal: `/tmp/skill.cncf.d/cncf-sbt-3693dfe7e7bdc16112a51023580aa0cb291ca56613b561f10e909202ac9d42a8-d99075495ef5e55a30a9a48ce4299ded/58358-20260928T032930Z.log`
- CNCF acceptance: `/tmp/skill.cncf.d/cncf-sbt-2be196066b0de778b2d5930773ab57fd543d5da3367f7277546c0eee465eecd1-d68e1446696aff7c0470b7e54c1bec0e/58611-20260928T033056Z.log`
- Artifact/classpath audits: `model-published-artifact-inspection.json` and
  `cncf-consumer-inspection.json` beside the parent workflow ledger.

Current Incident Blocker: resolved. Root cause confirmed: shared generator
runtime emitters, combined with an unconditional CNCF bridge/descriptor route,
made pure library output depend on CNCF. The original source/dependency
reproduction now succeeds through both native controls and the standard bridge.
This is a focused repair verified across the three repositories and consumer;
it is not a complete ecosystem/release validation.

### Changed files and ownership

Cozy changes:

- `src/main/scala/cozy/Cozy.scala`: target admission and source preflight.
- `src/main/scala/cozy/modeler/Modeler.scala`: target propagation and category checks.
- `src/main/scala/cozy/modeler/ScalaGenerator.scala`: pure/CNCF emission boundary.
- `src/main/scala/cozy/modeler/ModelGenerationTarget.scala`: explicit target and diagnostics.
- `src/main/scala/cozy/runtime/CozySbtBridge.scala`: target-specific bridge dispatch and guards.
- `docs/spec/library-generation-contract.md` and
  `docs/design/library-generation-contract.md`: intended semantics and implementation boundary.
- `src/test/scala/cozy/modeler/LibraryGenerationSpec.scala` and
  `src/test/scala/cozy/runtime/CozySbtBridgeLibraryGenerationSpec.scala`:
  native/JSON reproduction and rejection coverage.
- `src/test/resources/modeler/library/address.cml` and
  `src/test/resources/modeler/library/user-profile-values.cml`: exact input copies.

sbt-cozy changes:

- `src/main/scala/org/goldenport/cozy/CozyPlugin.scala`: target policy,
  version agreement, command routing, descriptor/provenance selection, state.
- `src/test/scala/org/goldenport/cozy/CozyDelegatedGeneratorSpec.scala`:
  library/default/explicit-CNCF version and dispatch contracts.
- `src/test/scala/org/goldenport/cozy/CozyGenerationProvenanceIntegrationSpec.scala`:
  current-contract fixture correction with real aggregate-v2 CAR packaging.
- `README.md`: public generation-target configuration.

Model changes:

- `build.sbt`: standard pinned library generation and removal of CNCF dependency.
- `project.yaml`: exact Cozy version and removal of CNCF declaration.
- `README.md`: generation ownership and supported library categories.
- This journal: cause, design decisions, exact identities, validation, and limits.

The development-workstation runtime-ownership journal receives a follow-up link.
Core, CNCF, SimpleModeler, CML inputs, and submodule pointers are unchanged.
No commit, push, remote publication, release conversion, or deployment occurred.
The source changes remain uncommitted in Cozy, sbt-cozy, and the model; all
existing concurrent workstation changes are preserved.

### Hygiene ledger

Nonblocking observations remain outside this repair: 27 existing pending model
tests; model deprecation warnings; Cozy's deprecated JST timezone identifier;
CNCF pattern-match warnings in `PlannedTransitionValidationHook.scala` and
`UnitOfWorkProgramPlanning.scala`. They were not repaired to extend this incident
batch. All touched repositories pass `git diff --check`.

### Development Candidate ledger and limits

No new Development Candidate was admitted. Broader Cozy and sbt-cozy full suites,
plugin scripted tests, CNCF full tests, assembly, and deployment were not run.
The model full suite was selected because removing its main dependency changes
the library-wide classpath; it includes the existing focused generated-value
and holder regressions. The exact SNAPSHOT tool runtime and the locally refreshed
plugin remain prerequisites until these changes are committed and distributed.
Earlier Mac mini uv/Skill-install acceptance was not repeated for this Scala
change; workstation-managed Python ownership remains unchanged.

## Agent Usage Summary

The bounded cross-repository implementation used registered Terra high; serialized
SBT used registered Luna medium, and exact native controls used registered Luna
low. Parent model and effort are unavailable and remain unknown. There was no
runtime fallback. Counts come from the live ledger, including failed executions
and each resumed use of the same agent.

| Agent | Model | Effort | Type / roles / states | START / RESUME / DIRECT | Scope | Final outcome |
| --- | --- | --- | --- | --- | --- | --- |
| `/root` | unknown | unknown | parent-task; diagnosis/architecture/verification | 0 / 0 / 1 | cause, frozen contract, diff and artifact audit, journal | root cause confirmed; final evidence/journal recorded |
| `/root/library_cncf_independence_fix` | gpt-5.6-terra | high | cncf_fix_worker_terra; FIX | 1 / 9 / 0 | Cozy, sbt-cozy, model frozen batch and bounded corrections | frozen source/fixture batch applied; no validation executed |
| `/root/sbt_attempt_db669c4fdeec1cdfb247` | gpt-5.6-luna | medium | cncf_command_runner; SBT | 1 / 0 / 0 | Cozy focused library+CNCF tests and runtime classpath export | failed; lock released; Modeler new call IModel/KaleidoxModel dispatch |
| `/root/sbt_attempt_ddb0e1eefea8191bbd4c` | gpt-5.6-luna | medium | cncf_command_runner; SBT | 1 / 0 / 0 | cozy | failed; lock released; 65/67 passed |
| `/root/sbt_attempt_d833f2cfbe2c42d25444` | gpt-5.6-luna | medium | cncf_command_runner; SBT | 1 / 0 / 0 | Cozy10suite regression and classpath export after native-preflight repair | success; lock released; 67/67 passed |
| `/root/sbt_attempt_9c9182f19d12531cf077` | gpt-5.6-luna | medium | cncf_command_runner; SBT | 1 / 0 / 0 | plugin tests first | failed; lock released; 0/0 passed |
| `/root/library_native_cli` | gpt-5.6-luna | low | cozy_command_runner; RUNTIME | 1 / 1 / 0 | Address/profile exact launcher controls | both native CML controls succeeded |
| `/root/sbt_attempt_4797c96be32198043a7c` | gpt-5.6-luna | medium | cncf_command_runner; SBT | 1 / 0 / 0 | plugin tests second | failed; lock released; 43/44 passed |
| `/root/sbt_attempt_8c36efe48b19f211d21a` | gpt-5.6-luna | medium | cncf_command_runner; SBT | 1 / 0 / 0 | plugin validation with exact CAR fixture authority | failed; lock released; 43/44 passed; Existing CAR fixture Scala target differs from generation provenance |
| `/root/sbt_attempt_cdaea271d9f8d639f2ea` | gpt-5.6-luna | medium | cncf_command_runner; SBT | 1 / 0 / 0 | 44 plugin tests including actual CAR integration | failed; lock released; 43/44 passed; Existing CAR fixture redundant scala path segment; all output content hashes matched |
| `/root/sbt_attempt_fb3bc6cc9b43f978e154` | gpt-5.6-luna | medium | cncf_command_runner; SBT | 1 / 0 / 0 | 44 plugin tests and CAR integration after fixture path correction | failed; lock released; 43/44 passed; Existing fixture v1 CLI validator contradicts aggregate v2; legacy CAR projection name |
| `/root/sbt_attempt_994ff9b9905ddcb95651` | gpt-5.6-luna | medium | cncf_command_runner; SBT | 1 / 1 / 0 | Plugin tests including current CAR ABI and aggregate-provenance package validation | success; lock released; 44/44 passed; original locator restored before execution |
| `/root/sbt_attempt_c56ba807572e970ed20a` | gpt-5.6-luna | medium | cncf_command_runner; SBT | 1 / 0 / 0 | Plugin dependency-artifact-refresh publishLocal exact unchanged SNAPSHOT | success; lock released |
| `/root/sbt_attempt_8d886543339af77e96b2` | gpt-5.6-luna | medium | cncf_command_runner; SBT | 1 / 0 / 0 | Actual model standard bridge generation, full existing tests and Compile dependencyClasspath | success; lock released; 67/67 passed; 27 pending |
| `/root/sbt_attempt_3693dfe7e7bdc16112a5` | gpt-5.6-luna | medium | cncf_command_runner; SBT | 1 / 0 / 0 | Model dependency-artifact-refresh publishLocal exact unchanged SNAPSHOT | success; lock released |
| `/root/sbt_attempt_2be196066b0de778b2d5` | gpt-5.6-luna | medium | cncf_command_runner; SBT | 1 / 0 / 0 | CNCF CML generation compile and actual dependencyClasspath after independent model JAR refresh | success; lock released |
