# address-cml type roster

updated_at=2026-04-05
status=in_progress
target=src/main/cozy/address.cml

## Purpose

This document records the current Stage 2 working classification of the
Address-family types defined in `address.cml`.

It is not the final freeze decision by itself.
Its role is to make the current type roster explicit so that Stage 2 can
review each type as:

- final candidate
- provisional
- remove candidate

The current Stage 2 policy is:

- types required for Japanese operation remain part of the maintained core
- those types may still be optional at the attribute level

## Scope

Current types defined under `# VALUE`:

- `Address`
- `CountryCode`
- `PostalCode`
- `Region`
- `Locality`
- `SubLocality`
- `StreetAddress`
- `ExtendedAddress`

## Working classification

### Final candidates

#### `Address`

- role: root Value Object
- reason: this is the document root and the maintained aggregate boundary

#### `CountryCode`

- role: reusable identifier value
- reason: it anchors international interpretation and already carries stable
  shared constraints

#### `PostalCode`

- role: reusable postal identifier value
- reason: it is a standard postal-address element and is used across
  downstream mappings even when country-specific validation remains external

#### `Region`

- role: reusable administrative-area value
- reason: it is needed for schema.org / vCard aligned mapping and is stable as
  a generic address concept

#### `Locality`

- role: reusable city-or-equivalent value
- reason: it is a core portable address concept with stable mapping value

#### `StreetAddress`

- role: reusable street-line value
- reason: it is essential for the minimum maintained Address shape

#### `SubLocality`

- role: reusable sub-city or district value
- reason: it is needed for Japanese address operation and therefore remains in
  the maintained core, while still being optional at the attribute level

#### `ExtendedAddress`

- role: reusable supplementary address-detail value
- reason: it is needed for Japanese address operation and detailed delivery
  scenarios and therefore remains in the maintained core, while still being
  optional at the attribute level

### Provisional

None at the current Stage 2 working review.

### Remove candidates

None at the current Stage 2 working review.

This means the current `address.cml` roster does not contain an obvious
removal candidate and does not currently contain a provisional type awaiting
scope classification.

## Notes for Stage 2

- The maintained core shape currently reads as
  `Address + CountryCode + PostalCode + Region + Locality + SubLocality + StreetAddress + ExtendedAddress`
- `SubLocality` and `ExtendedAddress` remain optional in `Address`, but they
  are no longer treated as out-of-core extensions
- Their modeling reason should be stated explicitly in the literate sections,
  not only implied by examples

## Next decisions required

- Rewrite `address.cml` narrative so that the maintained core / optional
  distinction is explicit
- Decide whether downstream mapping text should treat `SubLocality` as a
  first-class integration concern or as policy-driven supplemental mapping
- Decide how `ExtendedAddress` is described against vCard `EXT` and other
  projection targets
- Rewrite any baseline wording in `address.cml` that no longer matches the
  final role of the maintained optional types
