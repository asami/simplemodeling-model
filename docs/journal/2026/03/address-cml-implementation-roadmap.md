# address-cml implementation roadmap

updated_at=2026-03-25
status=completed
target=src/main/cozy/address.cml

## Stage status

This document records the Stage 1 roadmap and baseline closure for
`address.cml`.

It should be read as the roadmap for the first executable milestone, not as
the active completion ledger for the model.

The active Stage 2 completion ledger is:

- [address-cml-stage2-checklist.md](/Users/asami/src/dev2026/simplemodeling-model/docs/phase/2026/04/address-cml-stage2-checklist.md)

## Objective

The `Address` Value Object is stably generated from `address.cml`, and the
generated output is usable in `simplemodeling-model` and `CNCF`.

In this work, treat `address.cml` as the reference case for the following
three layers.

- Structural DSL
- Metadata DSL
- Narrative

## Frozen specification

The following contract is now frozen by the `address.cml` baseline and the
corresponding Cozy / CNCF regressions.

### `VALUE` model

- `VALUE Address` is the root Value Object
- `VALUE`s nested under `Address` are owned Value Objects
- `VALUE`s defined outside `Address` are independent reusable Value Objects
- `Address` attributes may reference both owned and independent Value Objects

### `ATTRIBUTE` bodies

- `ATTRIBUTE` accepts `table`, `dl` / bullet list, `yaml`, and `hocon` bodies
  in that order of precedence
- all accepted bodies are normalized into records before model generation

### Descriptive sections under `# VALUE`

- `HEADLINE`, `BRIEF`, `SUMMARY`, `DESCRIPTION`, `LEAD`, `CONTENT`,
  `ABSTRACT`, `REMARKS`, and `TOOLTIP` are reserved descriptive sections
- all reserved descriptive sections are optional
- reserved descriptive sections are narrative, not Value definitions

### Narrative boundaries

- free narrative is allowed before the first structural definition and after
  the last structural definition inside structural sections
- the same rule applies to the document-level prelude and post-structural
  sections
- narrative inside structural sections is preserved as source material for
  `help`, `explain`, diagnostics, and documentation generation

### Help projection

- `help` is ultimately consumed by CNCF `HelpProjection`
- `cozy` and the Scala generator provide the source metadata only
- `SUMMARY` is the primary short-form help source
- `DESCRIPTION` is the detail source
- narrative is the fallback explanatory source

### Generator contract

- `cozyGenerate` emits Scala source for `Address`, owned `VALUE`s, independent
  reusable `VALUE`s, `POWERTYPE`, and `STATEMACHINE`
- generated output is written stably under `target/scala-*/src_managed/main`
- regeneration produces stable diffs
- generated Scala classes carry Scaladoc derived from the model narrative
- the class-level Scaladoc uses the model description as the primary source
- attribute documentation is emitted as `@param` entries when a field has
  narrative text available
- the same Scaladoc projection applies to `VALUE`, `POWERTYPE`, and
  `STATEMACHINE` outputs in the Address family
- the same shared projection also applies to `ENTITY` and `COMPONENT` outputs
  that use the Scala class emitter

Example projection:

```scala
/**
 * Address is a structured postal destination value.
 * It represents address semantics as composable typed fields, not a single
 * formatted string.
 *
 * @param addressCountry ...
 * @param postalCode ...
 * @param streetAddress ...
 */
case class Address(...)
```

### CNCF integration contract

- generated VOs can be passed through at least one CNCF command/query path
- runtime validation happens at `Request -> Action` creation time
- `Builder.withX(...)` validates input values
- `Builder.build()` validates completion conditions

## Non-goal

- Do not design an overly generalized DSL too early
- Do not force `VALUE` into the existing `ENTITY` concept
- Do not expand to all models other than the Address family at once

## Deliverables

1. Frozen requirements for the `Address` Value Object in `address.cml`
2. Cozy `VALUE` / `POWERTYPE` / `STATEMACHINE` support specification
3. Cozy parser/modeler/generator implementation
4. Generated output acceptance on the `simplemodeling-model` side
5. Minimal integration on the CNCF side
6. Fixture-based regression tests

## Workstreams

### 1. Baseline model freeze

Freeze the `Address` Value Object model in `address.cml` as the implementation baseline.

Modeling rule for this case:

- `VALUE Address` is the root Value Object
- `VALUE` blocks nested under `Address` are owned Value Objects
- `VALUE`s defined outside `Address` are independent reusable Value Objects
- `Address` `ATTRIBUTE`s may reference both owned and independent Value Objects
- `ATTRIBUTE` accepts `table`, `dl` / bullet list, `yaml`, and `hocon` bodies, in that order of precedence, and normalizes them into records
- descriptive sections under `# VALUE` follow SimpleModeling `DescriptiveAttributes`
- descriptive sections are optional and do not affect structural validity when omitted
- free narrative is allowed before the first structural definition and after the last structural definition inside structural sections
- the same free-narrative rule applies to document-level prelude and post-structural sections
- narrative inside structural sections is preserved as source material for `help`, `explain`, diagnostics, and documentation generation
- `help` is ultimately projected by CNCF `HelpProjection`; `cozy` and generator output only provide the source metadata
- `SUMMARY` is the primary short-form help source, `DESCRIPTION` is the detail source, and narrative is the fallback explanatory source

Reserved descriptive section names under `# VALUE`:

- `HEADLINE`
- `BRIEF`
- `SUMMARY`
- `DESCRIPTION`
- `LEAD`
- `CONTENT`
- `ABSTRACT`
- `REMARKS`
- `TOOLTIP`

Classification rule under `# VALUE`:

- reserved descriptive section names are narrative sections
- other `## ...` sections are interpreted as Value definitions
- free text and non-structural subsections before the first Value definition are prelude narrative
- free text and non-structural subsections after the last Value definition are trailing narrative

Baseline classification for the current `address.cml`:

| Layer | Frozen content |
| --- | --- |
| Structural DSL | `VALUE Address`, owned `VALUE`s under `Address`, independent `VALUE`s such as `CountryCode`, and their `ATTRIBUTE` definitions |
| Metadata DSL | `status`, `category`, `compatibility`, `standards`, and constraint metadata such as `length`, `pattern`, `standard` |
| Narrative | optional descriptive sections mapped from `DescriptiveAttributes`, free prelude/trailing narrative inside structural sections, literate sections such as `Overview`, `Background`, and `Internationalization`, and the source text that CNCF later projects into `HelpModel` |

Done criteria:

- The role classification of root `VALUE`, owned `VALUE`, independent `VALUE`, `POWERTYPE`, `STATEMACHINE`, attribute, constraints, examples, mapping, and validation is documented
- The policy for coexisting with the structural section without removing the narrative section is fixed
- The metadata to generate, such as the `CountryCode` constraint, is enumerated
- Optional descriptive sections are aligned with `DescriptiveAttributes`
- Prelude and trailing narrative handling inside structural sections is fixed
- CNCF `HelpProjection` can consume the generated help metadata from the `cozy` output
- Help selector semantics remain component/service/operation aligned with CNCF `meta.help`

### 2. Cozy grammar/spec update

Specify `VALUE`, `POWERTYPE`, and `STATEMACHINE` as first-class concepts.

Scope:

- top-level `VALUE`
- top-level `POWERTYPE`
- top-level `STATEMACHINE`
- `ATTRIBUTE` under `VALUE`
- nested `VALUE` under root `VALUE`
- independent reusable `VALUE`
- optional descriptive sections under `# VALUE`
- free narrative before and after structural definitions inside `# VALUE`
- attribute-level constraint metadata
- literate classification diagnostics

Done criteria:

- The accepted syntax is documented in the grammar note
- The literate model spec is updated with structural / metadata / narrative handling
- It is clearly stated that `VALUE` is an independent concept
- Reserved descriptive section names and their optionality are fixed
- Prelude/trailing narrative in structural sections is preserved and externally visible

### 3. Cozy parser/modeler implementation

Load the structural layer of `address.cml` into the AST.

Scope:

- Parse the `VALUE` section
- Parse the `POWERTYPE` section
- Parse the `STATEMACHINE` section
- Preserve ownership for nested `VALUE`s
- Distinguish independent reusable `VALUE`s from owned `VALUE`s
- Parse the `ATTRIBUTE` body using the accepted syntax priority:
  1. `table`
  2. `dl` / bullet list
  3. `yaml`
  4. `hocon`
  5. plain text fallback
- Ingest constraint metadata
- Preserve the section classification result

Done criteria:

- The parser accepts `address.cml`
- `Address`, owned `VALUE`s, and independent reusable `VALUE`s appear in the AST
- `pattern`, `min_length`, `max_length`, and similar fields are preserved losslessly
- Powertype and statemachine concepts are preserved in the AST without flattening

### 4. Scala generator for value objects

Define the minimum Scala generation contract.

First target:

- `Address`
- owned `VALUE`s under `Address`
- independent reusable `VALUE`s such as `CountryCode`
- `POWERTYPE` outputs for related classification models
- `STATEMACHINE` outputs for related lifecycle models

Generation expectations:

- stable package name
- deterministic class order
- a consistent policy for wrappers and case classes
- room to reflect constraints
- ownership-aware naming and placement for nested `VALUE`s
- reusable packaging for independent `VALUE`s

Done criteria:

- `cozyGenerate` emits Scala source for `Address`, owned `VALUE`s, independent reusable `VALUE`s, `POWERTYPE`, and `STATEMACHINE`
- Generated output is stably written under `target`
- Regeneration produces stable diffs

### 5. simplemodeling-model integration

Accept the generated artifacts in this repository.

Scope:

- build source discovery
- package / import consistency
- integration with handwritten foundation code

Done criteria:

- `sbt compile` passes
- Generated VOs can be referenced from handwritten code
- `sbt test` includes regression coverage

### 6. CNCF minimal integration

Make the generated Value Objects usable at the command/query boundary.

Scope:

- schema/export policy for generated VOs
- passing values through command input / query input
- entry point for the runtime validation hook

Done criteria:

- At least one command/query can use a generated VO
- The CNCF side accepts it without type breakage

### 7. Regression tests

Freeze the `Address` Value Object part of `address.cml` as a fixture.

Test layers:

- parser test
- model normalization test
- Scala generation snapshot test
- `simplemodeling-model` compile/test
- CNCF integration smoke test

Done criteria:

- Test failures make it possible to identify what broke when `address.cml` changes
- Literate input including the narrative section is continuously validated

## Execution order

1. Baseline model freeze
2. Cozy grammar/spec update
3. Cozy parser/modeler implementation
4. Scala generator implementation
5. `simplemodeling-model` integration
6. CNCF minimal integration
7. Regression tests and stabilization

## Immediate next actions

1. Freeze the root / owned / independent `VALUE` classification table in the roadmap and keep it as the baseline reference
2. Reconcile the `VALUE` / `POWERTYPE` / `STATEMACHINE` independent-concept policy between the Cozy spec and implementation
3. Identify the entry points in Cozy implementation that accept the `VALUE`, `POWERTYPE`, and `STATEMACHINE` sections
4. Decide the minimal Scala output sample

## Risks

1. Even if `VALUE` is accepted by the parser, the existing generator is `ENTITY`-centric and may block later stages
2. Generalizing constraint metadata too quickly will slow the `Address`-first implementation
3. Expanding CNCF integration too early will lock in an unstable Cozy design

## Decision rule

When implementation decisions are unclear, prioritize in this order.

1. Do not break the literate model of `address.cml`
2. Keep `VALUE` as an independent concept
3. Preserve determinism in the generated output
4. Start CNCF integration at the minimum viable level
