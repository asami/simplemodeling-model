# Resource Activation Lifecycle Note

## Intent

`activatedAt`, `deactivatedAt`, and `expiresAt` are useful common attributes for `SimpleEntity` resources, but timestamps alone are not sufficient to express lifecycle semantics.

The model also needs an activation-oriented state machine so that the current usable state of a resource is explicit.

## Why Timestamps Alone Are Not Enough

With only timestamps, the model does not answer these questions clearly:

- Is the resource currently active or only scheduled to become active?
- Does `deactivatedAt` mean the resource is permanently disabled or only currently inactive?
- Does passing `expiresAt` automatically change the effective state to expired?
- Can a deactivated or expired resource be re-activated?

Those questions belong to lifecycle semantics, not merely data capture.

## Proposed Common Model Direction

The common location should be `ResourceAttributes`.

`ResourceAttributes` should carry two kinds of information:

- lifecycle timestamps
  - `activatedAt`
  - `deactivatedAt`
  - `expiresAt`
- lifecycle state
  - activation-oriented state machine

## Candidate State Machine

A provisional common state machine can be expressed as:

- `inactive`
- `active`
- `deactivated`
- `expired`

Possible transitions:

- `inactive -> active`
- `active -> deactivated`
- `active -> expired`
- `deactivated -> active` (policy-dependent)
- `expired -> active` (renewal policy-dependent)

This is intentionally small. The goal is to capture the ordinary resource lifecycle without making the common model too heavy.

## Design Principle

The common model should distinguish:

- timestamps: when lifecycle events happened
- state machine: what the resource currently means operationally

In other words:

- attributes answer `when`
- state machine answers `what state the resource is in now`

## Scope

This lifecycle is intended for common reusable resource semantics on `SimpleEntity`.

It is not intended to replace domain-specific state machines such as:

- `UserAccountStatus`
- workflow-specific approval state
- publication workflow state

Those remain domain-level or entity-level state machines.

## Current Position

At the current stage:

- `activatedAt`
- `deactivatedAt`
- `expiresAt`

have been introduced into `ResourceAttributes` / `ResourceAttributesUpdate` / `ResourceAttributesQuery`.

The activation lifecycle state machine is not yet implemented.

## Next Step

The next step is to decide where the common activation state machine type should live:

- under `org.simplemodeling.model.value.ResourceAttributes`
- or under `org.simplemodeling.model.statemachine`

The latter is likely cleaner if the state machine is intended to be reused across multiple common resource-oriented models.


## Responsibility Split With Other SimpleEntity State Machines

The activation lifecycle state machine is a common resource-level state machine.
Its role is intentionally narrower than domain-specific entity state machines.

### Activation Lifecycle State Machine

This common state machine answers:

- Is the resource usable now?
- Has it been activated yet?
- Has it been explicitly deactivated?
- Has it expired by lifecycle policy?

Its scope is limited to generic resource availability semantics.

### Domain-Specific Entity State Machines

Entity-specific state machines answer domain semantics such as:

- business progression
- account qualification
- review or approval flow
- contractual or legal state
- operational workflow state

Examples:

- `UserAccountStatus`
  - `provisional`
  - `registered`
  - `formal`
  - `suspended`
- publication workflow
  - draft / published / archived
- business process state
  - requested / approved / rejected / completed

These state machines express business meaning, not generic resource availability.

### Intended Division of Responsibility

The common activation lifecycle should own:

- generic availability
- activation timing
- deactivation timing
- expiry timing

Entity-specific state machines should own:

- domain progression
- business qualification
- service-specific or workflow-specific semantics

### Non-Goal

The activation lifecycle state machine should not absorb domain semantics.

For example, it should not try to replace:

- `UserAccountStatus.suspended`
- approval workflow states
- publication workflow states

If a `UserAccount` is `formal` but has been deactivated, both facts may be true at once:

- domain state machine: `formal`
- common activation lifecycle: `deactivated`

This is expected. They answer different questions.

### Interpretation Rule

A practical interpretation order is:

1. domain-specific state machine answers what the object means in the domain
2. common activation lifecycle answers whether the resource is operationally usable now

This means operational access may be denied even when the domain state is otherwise valid.

### Summary

The common activation lifecycle is orthogonal to domain-specific state machines.

- common activation lifecycle: reusable resource availability semantics
- entity state machine: domain meaning and progression

Keeping these separate preserves reuse while preventing the common model from becoming overloaded with business semantics.


## Alignment With Existing Common Lifecycle Concepts

The activation lifecycle should be aligned with the existing common lifecycle structures rather than merged into them.

### LifecycleAttributes

`LifecycleAttributes` already carries:

- `createdAt`
- `updatedAt`
- `createdBy`
- `updatedBy`
- `postStatus`
- `aliveness`

This layer should continue to represent foundational lifecycle and management state:

- creation and update history
- broad operational existence state
- publication-oriented workflow marker through `postStatus`

### Aliveness

`Aliveness` currently represents:

- `Alive`
- `Suspended`
- `Dead`

This should be interpreted as a coarse system-level or management-level existence state.
It is more fundamental than resource activation.

A resource may be:

- `Alive` but not activated yet
- `Alive` but deactivated
- `Alive` but expired

Therefore activation should not be absorbed into `Aliveness`.

### PublicationAttributes

`PublicationAttributes` currently represents publication and exposure timing:

- `publishAt`
- `publicAt`
- `closeAt`
- `startAt`
- `endAt`

This is about publication and visibility scheduling.
It answers whether something is within its publication window, not whether the resource itself is operationally active.

Therefore activation should not be merged into `PublicationAttributes` either.

### ResourceAttributes

`ResourceAttributes` is the correct common place for activation semantics because it can represent generic resource availability without mixing:

- audit and management lifecycle
- publication lifecycle
- domain-specific workflow lifecycle

Its responsibility is:

- activation timing
- deactivation timing
- expiry timing
- activation-oriented operational usability

### Recommended Interpretation

The common lifecycle concepts should be read as separate axes:

- `LifecycleAttributes.aliveness`: broad existence / management state
- `LifecycleAttributes.postStatus`: publication workflow state
- `PublicationAttributes`: publication window and exposure timing
- `ResourceAttributes.activation`: resource usability state

These axes may all be true at once and should not be collapsed into a single state machine.

### Example

A resource can legitimately be:

- `aliveness = Alive`
- `postStatus = Published`
- `publication window = open`
- `activation lifecycle = Deactivated`

This means:

- the object exists
- it is part of the published workflow
- the publication window is valid
- but the resource is currently not operationally usable

That combination is coherent and is one of the reasons the activation lifecycle should remain separate.
