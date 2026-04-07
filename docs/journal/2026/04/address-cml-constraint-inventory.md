# address-cml constraint inventory

updated_at=2026-04-05
status=in_progress
target=src/main/cozy/address.cml

## Purpose

This note records the current Stage 2 constraint classification for the
Address-family Value Objects.

The goal is to make explicit which constraints are:

- machine-readable in the core model
- narrative-only in the core model
- delegated to downstream policy via `AddressClassI18nContext`

This is a journal-layer design record.
It does not by itself freeze the final executable contract.

## Core rule

The core Address model should carry only constraints that are:

- cross-country
- stable across maintained integrations
- meaningful for reusable Value Object generation

Country-specific postal rules, formatting rules, and delivery-completeness
rules remain downstream.

The intended runtime shape is:

- generic `I18nContext` does not know `Address`
- `Address` handling obtains an `AddressClassI18nContext` from that generic
  context
- address-specific constraint, formatting, and projection behavior is resolved
  through `AddressClassI18nContext`

## Inventory

### `Address`

#### machine-readable in core

- `addressCountry` multiplicity = `1`
- `streetAddress` multiplicity = `1`
- `postalCode` multiplicity = `?`
- `addressRegion` multiplicity = `?`
- `addressLocality` multiplicity = `?`
- `addressSubLocality` multiplicity = `?`
- `extendedAddress` multiplicity = `?`

#### narrative-only in core

- `Address` is a structured postal destination value, not a formatted string
- `Address` is the canonical internal model
- maintained-core fields may still be optional in an instance

#### downstream policy

- country-specific completeness rules
- whether `postalCode` is required for a given country
- whether `addressRegion` is required for a given country
- how `addressSubLocality` participates in projection and recovery
- how `extendedAddress` participates in projection and recovery
- all of the above are intended to be resolved through
  `AddressClassI18nContext`

### `CountryCode`

#### machine-readable in core

- `value` multiplicity = `1`
- `min = 2`
- `max = 2`
- `pattern = ^[A-Z]{2}$`

#### narrative-only in core

- semantic intent is ISO 3166-1 alpha-2 country identity

#### downstream policy

- whether to validate against a current real-country code registry snapshot
- alias or historical-code handling
- these checks are intended to be resolved through `AddressClassI18nContext`

### `PostalCode`

#### machine-readable in core

- `value` multiplicity = `1`
- `min = 1`

#### narrative-only in core

- postal or ZIP code semantics
- the fact that postal-code format is country-dependent

#### downstream policy

- country-specific pattern validation
- normalization such as hyphen insertion/removal
- delivery-validity checks
- these checks are intended to be resolved through `AddressClassI18nContext`

### `Region`

#### machine-readable in core

- `value` multiplicity = `1`
- `min = 1`

#### narrative-only in core

- top-level administrative division semantics
- interpretation as state / prefecture / province

#### downstream policy

- controlled vocabulary per country
- canonical spelling rules
- these checks are intended to be resolved through `AddressClassI18nContext`

### `Locality`

#### machine-readable in core

- `value` multiplicity = `1`
- `min = 1`

#### narrative-only in core

- city-or-equivalent semantics

#### downstream policy

- locality vocabulary and normalization per country
- these checks are intended to be resolved through `AddressClassI18nContext`

### `SubLocality`

#### machine-readable in core

- `value` multiplicity = `1`
- `min = 1`

#### narrative-only in core

- subdivision-within-locality semantics
- maintained-core status for Japanese operation even though usage remains optional

#### downstream policy

- whether a given country uses this field
- mapping into external standards that lack a stable dedicated slot
- these checks are intended to be resolved through `AddressClassI18nContext`

### `StreetAddress`

#### machine-readable in core

- `value` multiplicity = `1`
- `min = 1`

#### narrative-only in core

- primary street-level address semantics

#### downstream policy

- local formatting and tokenization rules
- country-specific street-line validation
- these checks are intended to be resolved through `AddressClassI18nContext`

### `ExtendedAddress`

#### machine-readable in core

- `value` multiplicity = `1`
- `min = 1`

#### narrative-only in core

- supplementary address-detail semantics
- maintained-core status for Japanese operation even though usage remains optional

#### downstream policy

- projection policy for standards that lack a stable dedicated slot
- formatting and recovery policy across integrations
- these checks are intended to be resolved through `AddressClassI18nContext`

## Current freeze candidate

The current Stage 2 freeze candidate is:

- keep multiplicity machine-readable for all maintained Address-family types
- keep `CountryCode` shape constraints machine-readable
- keep non-empty constraints machine-readable for the Address-family string
  wrapper Value Objects
- keep all other per-country or per-format constraints out of the core model

This means the current core model is conservative about country-specific
rules, but not weak about basic Value Object quality.
Address-family wrapper Value Objects are expected to reject empty string
content as part of their own stable contract.

The intended extension point for those deferred rules is
`AddressClassI18nContext`, not direct specialization of the generic
`I18nContext`.

## Open questions

- Should ISO 3166-1 identity remain narrative-backed, or should the standard
  reference become explicit machine-readable metadata in the model?

## Next action

- reflect the freeze candidate into `address.cml` only where the team wants a
  stable executable contract now
- leave downstream-policy constraints in narrative unless and until a stable
  cross-country rule is agreed
- document `AddressClassI18nContext` as the Address-specific resolution point
  for downstream rules
