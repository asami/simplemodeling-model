# address class i18n context note

updated_at=2026-04-05
status=in_progress
target=src/main/cozy/address.cml

## Purpose

This note defines the intended responsibility split between:

- generic `I18nContext`
- generic `ClassI18nContext`
- `AddressClassI18nContext`

The goal is to support Address-specific validation, formatting, and projection
without making the generic i18n layer aware of `Address`.

## Core decision

`I18nContext` itself should not know `Address`.

Address-specific handling must be obtained through a class-specific context.
The intended flow is:

1. runtime holds a generic `I18nContext`
2. Address handling asks that context for an `AddressClassI18nContext`
3. Address-specific country-sensitive rules are resolved there

## Responsibility split

### `I18nContext`

Role:

- generic internationalization / localization context

May contain:

- locale
- country or region identity
- timezone
- generic policy lookup capability
- factory/access point for class-specific i18n contexts

Must not contain:

- Address-specific validation logic
- Address-specific formatting logic
- Address-specific mapping logic
- field-by-field postal-address rules

### `ClassI18nContext`

Role:

- generic class-scoped policy surface derived from `I18nContext`

Purpose:

- keep the generic i18n layer decoupled from domain classes
- provide a stable extension point for class-specific handling

This note does not require a rich common interface.
It only requires the idea that class-specific contexts are derived from the
generic context instead of being embedded into it.

### `AddressClassI18nContext`

Role:

- Address-specific internationalization and country-sensitive policy surface

Responsibilities:

- country-specific interpretation of Address-family fields
- country-specific completeness policy
- country-specific validation policy
- country-specific formatting policy
- country-specific projection/recovery policy for external standards

Examples:

- whether `postalCode` is required for a country
- how `PostalCode` should be pattern-validated for a country
- whether `SubLocality` is used for a country
- how `SubLocality` maps into `schema.org` or `vCard`
- how `ExtendedAddress` is projected or reconstructed

## What stays in the core model

The core model should continue to own:

- stable Value Object structure
- multiplicity
- cross-country reusable constraints
- the canonical meaning of the Address family

The core model should not own:

- country-specific postal formatting rules
- country-specific delivery validity
- country-specific completeness requirements
- integration-specific projection recovery rules

## Relationship to `address.cml`

In `address.cml`, the phrase "downstream policy" should be interpreted as:

- policy resolved through `AddressClassI18nContext`

It should not be interpreted as:

- ad hoc application code with no stable abstraction

## Relationship to CNCF

CNCF may hold a richer runtime `I18nContext`, but the `simplemodeling-model`
layer should depend only on the class-context contract it needs.

Therefore:

- `simplemodeling-model` should not depend on CNCF-specific runtime context
- CNCF may adapt or supply `AddressClassI18nContext` from its runtime
  `I18nContext`

## Freeze candidate

The current Stage 2 freeze candidate is:

- treat `AddressClassI18nContext` as the mandatory resolution point for
  country-sensitive Address rules
- keep generic `I18nContext` free of Address knowledge
- keep core Address generation independent from CNCF runtime types

## Open questions

- Should `ClassI18nContext` be a named common base type, or only a conceptual
  role?
- Should `AddressClassI18nContext` expose validation/formatting methods
  directly, or expose policy objects?
- Which minimum method surface is needed to support Stage 2 constraint and
  projection decisions?

## Next action

- use this split when deciding which constraints stay in core and which move
  to `AddressClassI18nContext`
- use this split when later designing CNCF integration for Address-specific
  validation and projection
