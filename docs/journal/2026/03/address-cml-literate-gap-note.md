# address-cml literate gap note

updated_at=2026-03-24
status=completed
target=src/main/cozy/address.cml

## Stage status

This note records the Stage 1 gap analysis and its baseline closure.

It should be read as the gap note for the first executable baseline, not as
the final statement that `address.cml` is complete in every respect.

The active Stage 2 completion ledger is:

- [address-cml-stage2-checklist.md](/Users/asami/src/dev2026/simplemodeling-model/docs/phase/2026/04/address-cml-stage2-checklist.md)

## Goal

With `address.cml`, make the following three layers coexist in a single document for the `Address` Value Object and related concepts.

- Structural DSL (executable)
- Metadata DSL (affects generated output)
- Narrative (literate explanation)

This gap note has been closed by the completed `address.cml` baseline and the
corresponding Cozy / CNCF regressions.

## Frozen specification summary

The following behaviors are now frozen and treated as the reference contract
for the Address family.

- `VALUE Address` is the root Value Object
- nested `VALUE`s under `Address` are owned Value Objects
- `VALUE`s outside `Address` are independent reusable Value Objects
- `ATTRIBUTE` bodies are accepted as `table`, `dl` / bullet list, `yaml`, and
  `hocon`, then normalized into records
- reserved descriptive sections under `# VALUE` are optional and map to
  `DescriptiveAttributes`
- narrative before and after structural definitions inside structural
  sections is preserved
- `help` is projected by CNCF `HelpProjection`
- `SUMMARY` / `DESCRIPTION` / narrative are the help priority order
- `cozyGenerate` emits `Address`, owned `VALUE`s, independent reusable
  `VALUE`s, `POWERTYPE`, and `STATEMACHINE`
- runtime validation happens during `Request -> Action` creation, with
  `Builder.withX(...)` and `Builder.build()` providing the two validation
  checkpoints
- generated Scala classes now carry Scaladoc derived from the literate model
  narrative
- the same narrative-to-Scaladoc projection applies to the Address family
  `VALUE`, `POWERTYPE`, and `STATEMACHINE` outputs
- the same projection also applies to generated `ENTITY` and `COMPONENT`
  outputs that use the shared Scala class emitter

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

## Current status (with latest cozy grammar)

- Executable structure can be represented with `COMPONENT` / `ENTITY` / `ATTRIBUTE`
- `ATTRIBUTE` can be described using YAML in the section body
- Narrative sections such as `Overview` / `Background` / `Mapping` can coexist and are non-executable

## Baseline freeze for `address.cml`

The current `address.cml` should be frozen as the first concrete example for
the `Address` Value Object family.

Modeling rule for this baseline:

- `VALUE Address` is the root Value Object
- `VALUE`s nested under `Address` are owned Value Objects
- `VALUE`s defined outside `Address` are independent reusable Value Objects
- `Address` `ATTRIBUTE`s may use both owned and independent Value Objects as types
- `ATTRIBUTE` bodies may be written as `table`, `dl` / bullet list, `yaml`, or `hocon`, and are normalized to records
- descriptive sections under `# VALUE` follow SimpleModeling `DescriptiveAttributes`
- descriptive sections are all optional
- reserved descriptive names are not interpreted as Value definitions
- free narrative is allowed before the first structural definition and after the last structural definition inside a structural section
- narrative inside structural sections is preserved for `help`, `explain`, diagnostics, and documentation generation
- `help` is consumed by CNCF `HelpProjection`; generated code only provides the source metadata
- `SUMMARY` is the primary short help source, `DESCRIPTION` is the detail source, and narrative is the fallback explanatory source

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
- these descriptive names are optional and may be omitted without affecting structural validity

### Structural DSL

- `VALUE Address`
- owned `VALUE`s under `Address`
- independent reusable `VALUE`s such as `CountryCode`

### Metadata DSL

- `status: draft`
- `category: value-object`
- `compatibility`
  - `schema.org PostalAddress`
  - `vCard ADR`
- `standards`
  - `ISO 3166-1`
  - `RFC 6350`
- `constraints`
  - `CountryCode.length = 2`
  - `CountryCode.pattern = ^[A-Z]{2}$`
  - `CountryCode.standard = ISO 3166-1 alpha-2`

### Narrative

- optional `DescriptiveAttributes` sections
- `HEADLINE`
- `BRIEF`
- `SUMMARY`
- `DESCRIPTION`
- `LEAD`
- `CONTENT`
- `ABSTRACT`
- `REMARKS`
- `TOOLTIP`
- `Overview`
- `Background`
- `Internationalization`

## Gaps observed from Address model

1. The first-class concept for Value Objects is weak
- At present, the model does not clearly distinguish root `VALUE`, owned `VALUE`, and independent reusable `VALUE`
- There is no first-class element that structurally expresses those ownership relationships

2. Machine interpretation of constraints is weak
- `CountryCode` `length/pattern` ends up treated as narrative or ad-hoc metadata
- Standard key contracts directly tied to validation code generation are not yet established

3. The role classification result of narrative sections cannot be exposed externally
- If the parser explicitly outputs classification results for structure / metadata / narrative, they become easier to reuse for AI and document generation

4. Reserved descriptive sections are not yet aligned with SimpleModeling standard attributes
- Without a fixed reserved list, `## SUMMARY` / `## DESCRIPTION` style sections can conflict with Value definition names
- The descriptive vocabulary already exists in `DescriptiveAttributes`, but the CML-side classification contract is not yet frozen

5. Structural-section narrative boundaries are not fixed enough
- Authors need to write explanation close to `VALUE`, `POWERTYPE`, and `STATEMACHINE` definitions
- Without explicit prelude/trailing narrative rules, nearby prose is either lost or ambiguously classified

## Extension candidates

1. Officially restore `VALUE`
- Goal: preserve Value Object intent as structure
- Keep `VALUE` in the AST as an independent first-class concept

2. Standardize attribute-level constraint metadata
- Example:
  - `min_length` / `max_length`
  - `pattern`
  - `format`
- Goal: align generated code with validation specifications

3. Emit a literate classification report
- Example: in the `cozyExplainModel` task, classify each section as
  - structural
  - metadata
  - narrative
  and emit the result

4. Align descriptive section vocabulary with `DescriptiveAttributes`
- treat `HEADLINE`, `BRIEF`, `SUMMARY`, `DESCRIPTION`, `LEAD`, `CONTENT`, `ABSTRACT`, `REMARKS`, and `TOOLTIP` as reserved narrative section names
- treat all of them as optional

5. Fix structural-section narrative boundaries
- allow free narrative before the first structural definition in a structural section
- allow free narrative after the last structural definition in a structural section
- preserve both as narrative context instead of discarding them

## Suggested rollout

1. Accept `VALUE` as a first-class concept in the parser / modeler / AST
2. Accept `POWERTYPE` and `STATEMACHINE` as first-class concepts in the parser / modeler / AST
3. Add ownership markers for nested `VALUE`s and preserve them in the Model AST
4. Add constraint keys to `ATTRIBUTE` YAML and preserve them in the Model AST
   - Accept `table`, `dl` / bullet list, `yaml`, and `hocon` bodies in that order of precedence, with plain text as a last-resort fallback
5. Freeze descriptive section names from `DescriptiveAttributes` as optional narrative sections
6. Freeze prelude and trailing narrative handling inside structural sections
7. Define the generator policy for reflecting them in Scala validation/annotations
8. Emit the classification report as diagnostics

---

## Powertype (same treatment)

Treat `POWERTYPE` the same way as Address, allowing the three layers
(Structural/Metadata/Narrative) to coexist in the same document.

### Gaps observed

1. The first-class concept for powertypes is weak
- The intent of `POWERTYPE` (classification axis / option set) tends to drift into narrative
- Structural responsibilities such as candidate enumeration, closed/open sets, and compatibility policy are not sufficiently preserved in the AST

2. The boundary with Value is unclear
- The usage policy between `VALUE` and `POWERTYPE` is not machine-readable
- Choosing the generated target type (enum / ADT / constrained string) tends to become ad hoc

3. Metadata key contracts are not unified
- Keys such as `codes` / `labels` / `deprecated` / `default` are not standardized enough

### Extension candidates

1. Fix `POWERTYPE` as a first-class concept
- Internal normalization may be allowed for compatibility, but the concept should remain in the AST

2. Standardize powertype metadata
- Example:
  - `codes`
  - `default`
  - `deprecated`
  - `open_set`

3. Fix the generation contract
- Document the powertype -> enum/ADT conversion rules
- Separate language-specific differences into generator policy

---

## StateMachine (same treatment)

Treat `STATEMACHINE` the same way as Address, allowing the three layers
(Structural/Metadata/Narrative) to coexist in the same document.

### Gaps observed

1. Structural and narrative are mixed together
- Descriptions of transition conditions and execution conditions (guards) are easy to mix
- The meaning of declaration order / priority tends to end up in narrative

2. Machine contracts for guard/action metadata are insufficient
- The distinction between expression / ref / composite guards is easy to blur
- Failure causes for action binding (undefined events, invalid transition targets) are hard to see in diagnostics

3. Event linkage visibility is insufficient
- It is hard to externally output trace results for linkage between `on` events and receiver definitions (Event/Subscription)

### Extension candidates

1. Standardize StateMachine metadata contracts
- Example:
  - `priority`
  - `declaration_order`
  - `guard.kind` (`expression` / `ref` / `composite`)
  - `action`

2. Strengthen validation diagnostics
- Return undefined events
- invalid transition targets
- guard/action resolution failures
with taxonomy/facet information

3. Emit a linkage report
- Emit the resolution results for statemachine -> event/subscription -> actioncall as diagnostics

---

## Unified rollout (VALUE / POWERTYPE / STATEMACHINE)

1. Fix structural aliases and normalization rules for VALUE, POWERTYPE, and STATEMACHINE
2. Standardize metadata key contracts for each section
3. Preserve the concepts in the AST and separate policy on the generator side
4. Externally emit literate classification and linkage diagnostics
