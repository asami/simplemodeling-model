# address-cml stage2 checklist

updated_at=2026-04-05
status=in_progress
scope=address.cml completion definition and finishing work

## Stage objective

Stage 2 defines what "complete" means for `address.cml` after the Stage 1
baseline.

This stage is not about re-proving that generation works at least once.
It is about fixing the remaining gap between:

- "baseline fixture is working"
- "`address.cml` is the completed reference model"

## Relation to 2026/03 version

The Stage 1 checklist is:

- [address-cml-checklist.md](/Users/asami/src/dev2026/simplemodeling-model/docs/phase/2026/03/address-cml-checklist.md)

Stage 1 should now be interpreted as:

- baseline freeze completed
- minimal Cozy integration completed
- minimal `simplemodeling-model` integration completed
- minimal CNCF smoke integration completed

Stage 2 is a successor checklist.
It does not replace the historical meaning of the 2026/03 file, but it does
replace it as the active completion ledger for `address.cml`.

Operationally:

- 2026/03 = baseline and first executable milestone
- 2026/04 = completion definition and remaining work ledger

## Completion criteria

`address.cml` is considered complete only when all of the following are true.

### 1. Model completion

- [ ] The `Address` family scope is fixed and no placeholder type remains
- [ ] Types required for Japanese operation are fixed as part of the maintained core even when they remain optional in `Address`
- [ ] Root `VALUE`, owned `VALUE`, and independent reusable `VALUE` roles are all explicit and stable
- [ ] Each generated type has a clear modeling reason, not just generator convenience
- [ ] Structural, metadata, and narrative layers can be read without ambiguity by a human maintainer

### 2. Literate completion

- [ ] The literate sections are intentionally authored, not just preserved fragments
- [ ] The descriptive sections under `# VALUE` are complete enough to support `help` and documentation generation
- [ ] Prelude and trailing narrative are intentionally used or intentionally absent
- [ ] Mapping, validation, and formatting narratives are either completed or explicitly marked out of scope

### 3. Constraint completion

- [ ] All important constraints in the Address family are machine-readable
- [ ] All constraints that affect runtime validation are represented in a stable way
- [ ] There is no known mismatch between narrative constraints and generated/runtime constraints

### 4. Generator completion

- [ ] Generated Scala output for the Address family is stable under regeneration
- [ ] Generated Scaladoc is acceptable as project-facing documentation
- [ ] Builder validation semantics are fixed for the Address family
- [ ] Package naming, type naming, and field naming are fixed

### 5. Integration completion

- [ ] `simplemodeling-model` accepts the generated output without fixture-only hacks
- [ ] CNCF can use generated VOs on at least one maintained command path and one maintained query path
- [ ] The generated help/documentation metadata is actually consumable by downstream projection

### 6. Regression completion

- [ ] The intended structure of `address.cml` is protected by regression tests
- [ ] The intended narrative projection is protected by regression tests where practical
- [ ] The intended validation behavior is protected by regression tests

## Open issues

These are the known unresolved items that still block calling `address.cml`
"complete".

### A. Completion semantics

- [ ] The project still lacks a single explicit completion decision for `address.cml`
- [ ] The older 2026/03 checklist still reads as fully completed, which can mislead readers

### B. Literate content quality

- [ ] The current document is strong enough for implementation work, but not yet clearly reviewed as the final literate text
- [ ] It is not yet fixed which narrative sections are normative, explanatory, or illustrative

### C. Structural finalization

- [ ] The final roster of Address-family types still needs explicit sign-off
- [ ] The maintained-core / optional distinction still needs to be made explicit in the model text
- [ ] Some sections were introduced as implementation-driving material and may still contain "baseline" wording rather than "final" wording

### D. Constraint and validation alignment

- [ ] There is still a risk that some constraints are only implied narratively
- [ ] The Address family has not yet been explicitly closed against future grammar adjustments

### E. Downstream contract

- [ ] It is not yet explicitly frozen which CNCF path is the maintained reference path for Address-family VO usage
- [ ] It is not yet explicitly frozen which help projection output is the approval target for the literate content

## Remaining tasks

### 1. Reclassify the stage documents

- [ ] Mark 2026/03 as Stage 1 / baseline milestone in surrounding references
- [ ] Treat this 2026/04 file as the active completion ledger

### 2. Freeze the Address family

- [x] Review the current `address.cml` type roster and mark each type as final / provisional / remove
- [ ] Remove or rewrite baseline-only wording inside the model where needed
- [ ] Make the maintained-core / optional distinction explicit for Japanese-operation-required types

### 3. Finish the literate sections

- [x] Review `Overview`
- [x] Review `Background`
- [ ] Review mapping-related sections
- [x] Review validation-related sections
- [ ] Review formatting-related sections
- [ ] Decide which sections are mandatory for "complete"

### 4. Freeze validation semantics

- [x] Enumerate all Address-family constraints that must be machine-readable
- [x] Record the intended Address-specific i18n resolution point for downstream constraints
- [ ] Verify the generated builder/runtime behavior against those constraints
- [ ] Document intentional gaps, if any remain

### 5. Freeze downstream usage contract

- [ ] Choose the maintained CNCF command path example
- [ ] Choose the maintained CNCF query path example
- [ ] Choose the maintained help/documentation projection example

### 6. Close regression gaps

- [ ] Add or adjust tests for final narrative/metadata assumptions where practical
- [ ] Add or adjust tests for final validation assumptions
- [ ] Add or adjust tests for final generator naming/output assumptions

## Active checklist

### Phase 1. Stage reinterpretation

- [ ] Clarify in project docs that 2026/03 is Stage 1, not final completion
- [ ] Link this 2026/04 file from nearby planning/checklist documents

### Phase 2. Model freeze

- [ ] Finalize the Address family type list
- [ ] Finalize the role of each `VALUE`
- [x] Finalize the first-pass attribute-level machine-readable constraints

### Phase 3. Literate freeze

- [ ] Finalize descriptive sections under `# VALUE`
- [ ] Finalize non-structural literate sections
- [ ] Finalize help-oriented summary/description text

### Phase 4. Generator and runtime freeze

- [ ] Confirm generated Scala output matches the final model intent
- [ ] Confirm validation behavior matches the final model intent
- [ ] Confirm CNCF maintained usage paths remain green

### Phase 5. Completion review

- [ ] Review all open issues and close or explicitly defer each one
- [ ] Confirm all completion criteria are satisfied
- [ ] Mark this file `status=completed`

## References

- [address-cml-checklist.md](/Users/asami/src/dev2026/simplemodeling-model/docs/phase/2026/03/address-cml-checklist.md)
- [address-cml-type-roster.md](/Users/asami/src/dev2026/simplemodeling-model/docs/phase/2026/04/address-cml-type-roster.md)
- [address-cml-constraint-inventory.md](/Users/asami/src/dev2026/simplemodeling-model/docs/journal/2026/04/address-cml-constraint-inventory.md)
- [address-class-i18n-context-note.md](/Users/asami/src/dev2026/simplemodeling-model/docs/journal/2026/04/address-class-i18n-context-note.md)
- [address-cml-implementation-roadmap.md](/Users/asami/src/dev2026/simplemodeling-model/docs/journal/2026/03/address-cml-implementation-roadmap.md)
- [address-cml-literate-gap-note.md](/Users/asami/src/dev2026/simplemodeling-model/docs/journal/2026/03/address-cml-literate-gap-note.md)
