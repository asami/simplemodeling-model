# migration-plan

## Migration categories (first draft)

1. Base types and validation helpers for Value Objects
2. Types around Entity/Id
3. Elements that stay within the model through Record/Tree conversion
4. Utilities that should remain in the model layer

## Acceptance rules

- Port code from `goldenport-core` with compatibility as the first priority
- After compatibility is secured, reorganize it gradually for `simplemodeling-model`
- For new types, prefer `cml` generated code by default and keep handwritten code to a minimum

## Generated integration notes

- Assume the generation target is under `target` (managed by `cozy`)
- Use `org.simplemodeling.model.generated` as the default package candidate
- Direct output to `src/main/scala` is prohibited so regeneration does not affect handwritten code
