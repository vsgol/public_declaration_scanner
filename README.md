# PublicDeclarationScanner

PublicDeclarationScanner is a tool that parses Kotlin source files and prints all public declarations found in them. It uses the Kotlin PSI (via `kotlin-compiler-embeddable`) for static analysis.

## Run

```bash
./gradlew run --args=./path/to/kotlin/code
```

## Output Format

- Shows `public` (or implicitly public) declarations
- Supports `class`, `interface`, `object`, `fun`, and `val/var`
- Displays nested declarations with indentation
- Shows modifiers like `abstract`, `open`, `inline`, `suspend`

## Testing

Run all tests:

```bash
./gradlew test --tests PublicDeclarationScannerTest
```

## Limitations

### Inferred Types

When a variable does not have an explicit type or lambda, the type cannot be inferred statically without full type resolution. Therefore, the tool prints following:

- If a lambda is detected as initializer: `: lambda`
- Otherwise, type: `: Any`

Example:

```kotlin
val x = 42       // becomes: val x: Any
val f = { it + 1 } // becomes: val f: lambda
```

### Known Missing Features

- Full type inference (requires compiler frontend)
- Handling of `@PublishedApi` internal declarations
- Display of class constructor parameters (e.g., `class A(val x: Int)`)
- JSON or Markdown output formats
- Command-line options (e.g., `--output`, `--help`)
