# address-cml checklist

updated_at=2026-03-25
status=completed
scope=address.cml -> Cozy -> simplemodeling-model -> CNCF

## Phase objective

Value Object generation from `address.cml` is working, and the `Cozy` and
`CNCF` implementations are aligned with the literate model.

## References

- [address-cml-implementation-roadmap.md](/Users/asami/src/dev2026/simplemodeling-model/docs/journal/2026/03/address-cml-implementation-roadmap.md)
- [address-cml-literate-gap-note.md](/Users/asami/src/dev2026/simplemodeling-model/docs/journal/2026/03/address-cml-literate-gap-note.md)

## Checklist

### 1. Baseline freeze

- [x] Finalize the structural section of `address.cml`
- [x] Finalize the metadata section of `address.cml`
- [x] Finalize the narrative section of `address.cml`
- [x] Document the constraint requirements for `CountryCode`
- [x] Finalize the list of types generated from `Address`
- [x] Freeze `DescriptiveAttributes`-aligned optional section names under `# VALUE`
- [x] Freeze prelude/trailing narrative rules inside structural sections

### 2. Cozy specification

- [x] Confirm the specification for treating `VALUE` as an independent concept
- [x] Organize the accepted syntax for `ATTRIBUTE` in the order `table` -> `dl` / bullet list -> `yaml` -> `hocon` -> text fallback
- [x] Freeze the first version of the constraint metadata key
- [x] Freeze the requirements for literate classification diagnostics
- [x] Position `address.cml` as the representative specification example
- [x] Define the classification rule for reserved descriptive section names under `# VALUE`
- [x] Define how structural-section narrative is preserved for `help` and `explain`
- [x] Define CNCF `HelpProjection` as the final consumer of generated help metadata
- [x] Fix the `summary` / `description` / narrative priority for help generation

### 3. Cozy parser/modeler

- [x] Identify the entry point for the `VALUE` section
- [x] Implement parsing for `VALUE`
- [x] Verify ingestion of the `ATTRIBUTE` body across `table`, `dl` / bullet list, `yaml`, and `hocon`
- [x] Preserve constraint metadata in the AST
- [x] Decide how section classification is preserved and emitted

### 4. Cozy generator

- [x] Include `Address` in the generation targets
- [x] Include wrapper types such as `CountryCode` and `PostalCode` in the generation targets
- [x] Fix the package name
- [x] Verify determinism of output ordering
- [x] Verify diff stability on regeneration

### 5. simplemodeling-model integration

- [x] Fix how generated source is imported
- [x] Make generated VOs referenceable from handwritten code
- [x] `sbt compile` を通す
- [x] `sbt test` を通す
- [x] Add a regression test for generated-source acceptance

### 6. CNCF minimal integration

- [x] Decide the minimal example for using generated VOs as command input
- [x] Decide the minimal example for using generated VOs as query input/output
- [x] Decide the entry point for the runtime validation hook
- [x] Add a smoke test on the CNCF side

### 7. Regression

- [x] Fix the `address.cml` fixture
- [x] parser test を追加する
- [x] model normalization test を追加する
- [x] Scala generation snapshot test を追加する
- [x] Add an end-to-end smoke test

## Exit criteria

- [x] `cozyGenerate` generates Address-related Scala source
- [x] `simplemodeling-model` can compile the generated source
- [x] `CNCF` can pass a generated VO through at least one path
- [x] The narrative-bearing `address.cml` is protected by regression tests
