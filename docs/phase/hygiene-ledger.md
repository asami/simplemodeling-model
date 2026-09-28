# Hygiene Ledger

## HYG-SMM-ENTITYID-HISTORY-COMMENT-001 — EntityId file-history comment placement

- Status: OPEN
- Affected source: `src/main/scala/org/simplemodeling/model/datatype/EntityId.scala`
- Observation: the file-history block for `EntityId` is placed after the
  file's first `object` declaration, `EntityCollectionIdPayload` (currently
  lines 90–97).
- Required outcome: place a file-history block after imports and before the
  file's first `class` or `object` declaration. Preserve all history entries
  and make no behavioral, API, serialization, or identity changes.
- Why this is Hygiene: the correction is documentation-only source layout and
  does not alter the `EntityId` contract.
- Intended follow-up: next SimpleModeling Model Hygiene batch.
- Focused validation: `git diff --check`; confirm that the change is limited
  to the history-comment placement.

## CNCF pattern check

Checked `cloud-native-component-framework` for the same pattern: a file-history
block after its first `class` or `object` declaration. No matching source file
was found, and CNCF does not contain a duplicate `EntityId` definition. No CNCF
Hygiene record is required.
