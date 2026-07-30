# AGENTS.md

This document provides guidelines for the development and operation of the ComicViewer project (SSoT Master).
General agent rules and codes of conduct are defined in the `.agent/` directory.

## References
- **Code of Conduct / Rules (What):** See `.agent/rules/`
- **Workflows (How):** See `.agent/workflows/`

## Table of Contents

- [Project Overview](#project-overview)
- [Technology Stack](#technology-stack)
- [Environment Setup](#environment-setup)
- [Commands](#commands)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Code Style](#code-style)
- [Git Workflow](#git-workflow)
- [Boundaries](#boundaries)
- [Security Considerations](#security-considerations)
- [Release Process](#release-process)
- [Troubleshooting](#troubleshooting)

---

## Project Overview

ComicViewer is a multi-platform comic viewer application supporting Android, iOS, and JVM (Desktop). It is developed using Kotlin Multiplatform and provides a modern UI using Jetpack Compose.

### Key Features

- Multi-platform support (Android, iOS, JVM)
- High maintainability with modular architecture
- Support for local storage and network storage (SMB)
- Support for multiple file formats (archives, documents)

---

## Technology Stack

- **Kotlin Multiplatform**: Main programming language
- **Jetpack Compose**: UI framework (Android, JVM, iOS)
- **Kotlin**: 2.4.10
- **Gradle**: 9.6.1
- **Java**: 21 (Required)
- **Android SDK**: compileSdk 37, minSdk 30
- **Modular Architecture**: feature/domain/data layer structure
- **Metro**: Dependency injection framework
- **Room**: Database (Android)
- **Coil**: Image loading library

---

## Environment Setup

### Prerequisites

1. **Install Java 21 (Required)**
    - Will not work with Java 17.
    - Set the `JAVA_HOME` environment variable to your Java 21 installation path.
    - Verification command: `java -version` should display OpenJDK 21+.

2. **Android SDK**
    - API 37 (compileSdk)
    - Minimum API 30 (minSdk)
    - Android SDK Build-Tools

3. **Network Access**
    - Access to Google Maven repository (dl.google.com) is required.
    - Maven Central (repo1.maven.org)
    - Gradle Plugin Portal (plugins.gradle.org)

### Initial Setup

```bash
# Check if Java 21 is active
java -version

# Clone the repository
git clone https://github.com/SorrowBlue/ComicViewer.git
cd ComicViewer

# Grant permissions to the Gradle wrapper
chmod +x gradlew
```

---

## Commands

### Basic Build Commands

```bash
# Clean build
./gradlew clean build

# Build applications
./gradlew :app:androidApp:build :app:jvmApp:build

# Check all modules
./gradlew check
```

### Platform-Specific Builds

```bash
# Android Debug
./gradlew :app:androidApp:assembleDebug

# Android Release
./gradlew :app:androidApp:assembleRelease

# JVM
./gradlew :app:jvmApp:packageDistributionForCurrentOS
```

### Test Commands

```bash
# Run all tests
./gradlew allTests

# Android Unit tests only
./gradlew :app:androidApp:testDebugUnitTest

# JVM Tests
./gradlew :app:jvmApp:jvmTest

# Test specific modules
./gradlew :domain:model:test
./gradlew :data:database:test
```

### Quality Check Commands

```bash
# Detekt (Static code analysis) - All platforms
./gradlew reportMerge

# Detekt for build-logic
./gradlew :build-logic:detektAll

# Code formatting
./gradlew detektFormat

# Android Lint - Debug
./gradlew :app:androidApp:lintDebug

# Android Lint - All build variants
./gradlew :app:androidApp:lintDebug
./gradlew :app:androidApp:lintInternal
./gradlew :app:androidApp:lintPrerelease
./gradlew :app:androidApp:lintRelease

# Version Catalog Lint
./gradlew versionCatalogLint
```

### Important Notices

- **NEVER cancel builds**: Make sure the timeout is set sufficiently long.
- **Network Access**: Access to Google Maven repositories is required. The build will fail in restricted environments.

---

## Code Style

### Basic Rules

1. **Kotlin Style Guide**
    - Follow Android's [Kotlin style guide](https://developer.android.com/kotlin/style-guide).
    - **Trailing commas** MUST always be used (arrays, function parameters, class declarations, etc.).
    - Function names: camelCase
    - Class names: PascalCase

2. **Package Structure**
    - All packages must use the `com.sorrowblue.comicviewer.*` prefix.
    - Include a newline at the end of every file.
    - Organize imports with `*` groups first, followed by `^` groups (enforced by detekt).

### Compose-Specific Rules

```kotlin
// ✅ Correct Example
@Composable
fun ScreenContent(
    title: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Prioritize Material3 components
    Button(
        onClick = onAction,
        modifier = modifier,
    ) {
        Text(text = title)
    }
}

// ❌ Avoid (No trailing commas)
@Composable
fun ScreenContent(
    title: String,
    onAction: () -> Unit
) {
    // ...
}
```

- Use named parameters for `@Composable` functions.
- Utilize default arguments and trailing lambdas.
- Prioritize Material3 components.
- Provide UI component previews using the `@Preview` annotation.
- Follow Compose naming conventions: `ScreenContent`, `ScreenState`, etc.
- Use Material3 Adaptive components for Android.
- Minimize recompositions through proper state management.

### Documentation Requirements

```kotlin
/**
 * Add KDoc comments to public APIs.
 *
 * @param userId User ID
 * @return User information
 */
fun fetchUser(userId: String): User {
    // Use Japanese or English comments for complex logic
    // Retrieve user information from the database
    return database.getUser(userId)
}

// TODO(username): Clearly specify the owner for planned features
```

- **Public APIs**: Add KDoc comments to all public functions and classes.
- **Complex Logic**: Add explanatory comments for business logic.
- **TODO**: Include owner information.
- **Architectural Decisions**: Document multi-platform expect/actual implementations.

---

## Testing

### Testing Standards

1. **Unit tests must be included for all new features.**
2. **Use Compose Testing Framework for UI tests.**
3. **Place test files in the same package structure as source files.**
4. **Use test doubles to mock external dependencies.**
5. **Test multi-platform code across all target platforms.**

### Test File Structure

```
src/
  main/kotlin/com/sorrowblue/comicviewer/feature/
    UserRepository.kt
  test/kotlin/com/sorrowblue/comicviewer/feature/
    UserRepositoryTest.kt
```

### Verification Scenarios

#### Android Verification

1. Run `./gradlew :app:androidApp:assembleDebug :app:androidApp:testDebugUnitTest`
2. Run `./gradlew :app:androidApp:lintDebug`
3. If there are UI changes, manually test core user flows.

#### JVM Verification

1. Run `./gradlew :app:jvmApp:packageDistributionForCurrentOS`
2. Test desktop-specific features (window management, filesystem access).
3. Verify multi-platform code works across all targets.

#### Cross-Platform Verification

1. Run `./gradlew reportMerge`
2. Run `./gradlew :domain:model:test :data:database:test`
3. Verify expect/actual implementations function correctly.

---

## Security Considerations

### Mandatory Checklist

1. **Input Validation**
    - Implement validation for all user inputs.
    - Prevent SQL Injection, XSS, and other attacks.

2. **Null Safety**
    - Leverage Kotlin's null safety features.
    - Design code with nullability in mind.

3. **External Libraries**
    - Use the latest stable versions.
    - Periodically check for known vulnerabilities.

4. **API Communication**
    - Include proper error handling.
    - Encrypt sensitive data in transit.

5. **Lifecycle Management**
    - Implement appropriate lifecycle management for Android.
    - Avoid memory leaks.

6. **Secret Management**
    - NEVER commit secrets (keys, tokens, passwords) to source code.
    - Use environment variables or GitHub Secrets.

### Secrets Used in GitHub Actions

- `ANDROID_STORE_FILE_BASE64`: Android keystore file
- `ANDROID_STORE_PASSWORD`: Keystore password
- `ANDROID_KEY_ALIAS`: Key alias
- `ANDROID_KEY_PASSWORD`: Key password
- `GOOGLE_WORKLOAD_IDENTITY_PROVIDER`: Google Cloud Workload Identity
- `GOOGLE_SERVICE_ACCOUNT`: Service account for Play Console access
- `DISCORD_WEBHOOK`: Webhook URL for Discord notifications

---

## Git Workflow

### Branch Naming Conventions

Convention: `[type]/[issue-number]-[brief-description-of-issue]`

**Types:**

- `feature/`: New features
- `enhancement/`: Improvements to existing features
- `refactor/`: Code improvements (no functional changes)
- `fix/`: Bug fixes
- `doc/`: Documentation creation, modifications, or additions
- `dependencies/`: Dependency updates
- `chore/`: Build, CI/CD, utility changes, etc.

**Examples:**

```
feature/123-add-bookmark-feature
fix/456-crash-on-startup
doc/789-update-readme
```

### Commit Message Standards

- **Written in English**
- **Conventional Commits format is recommended**

**Format:**

```
<type>: <subject>

<body>

<footer>
```

**Types:**

- `feat`: New features
- `fix`: Bug fixes
- `docs`: Documentation
- `style`: Formatting, missing semi colons, etc. (no code changes)
- `refactor`: Refactoring production code
- `test`: Adding or refactoring tests
- `chore`: Updating build tasks, package manager configs, etc.

**Example:**

```
feat: Add bookmark feature to comic viewer

Implement bookmark functionality that allows users to save
their reading progress.

Closes #123
```

### Issue & PR Language Guidelines

- **Issue Title**: Written in English
- **Issue Description & Comments**: Written in Japanese
- **PR Title**: Written in English
- **PR Description & Comments**: Written in Japanese
- **Conversations & Communication**: Conducted in Japanese

### Pre-PR Checklist

Execute the following before committing:

```bash
# 1. Code formatting
./gradlew detektFormat

# 2. Static code analysis
./gradlew reportMerge

# 3. Lint check
./gradlew :app:androidApp:lintDebug

# 4. Run tests
./gradlew allTests
```

### Writing PR Descriptions

1. Clearly describe the **summary of changes**.
2. Include the **related issue number** (`Fixed #123`).
3. Describe the **testing method**.
4. Include **screenshots** (if there are UI changes).
5. Specify any **breaking changes**.

**Template Example:**

```markdown
## Changes
Implemented the bookmark feature.

## Related Issues
Fixed #123

## Testing Method
1. Launch the app
2. Open a comic
3. Tap the bookmark button
4. Verify the bookmark is saved

## Screenshots
(If applicable)

## Checklist
- [x] Detekt executed
- [x] Lint executed
- [x] Tests executed
- [x] Documentation updated
```

### Label Management

- **Issue**: Select appropriate labels from those defined in `.github/labels.yml`.
- **PR**: Automatically assigned by Release Drafter, no manual intervention needed.

### Quality Gates

All PRs must pass:

1. **Lint**: Android Lint check
2. **Detekt**: Static code analysis
3. **Test**: Unit tests
4. **Build**: Successful build

These are executed automatically via `.github/workflows/lint-test-build.yml`.

---

## Boundaries

This section defines boundary conditions to accelerate decision-making during implementation.

### Always

- Add or update tests corresponding to modified code.
- Run quality checks (detekt/lint/test) listed in `AGENTS.md` before creating a PR.
- Adhere to module dependency rules (feature/domain/data/framework).

### Ask First

- Add new dependency libraries or plugins.
- Make changes affecting public APIs, database schemas, or CI configurations.
- Undertake large-scale refactoring or module reorganization.

### Never

- Commit secrets (keys, tokens, passwords) to source code.
- Merge without resolving deleted tests.
- Add direct dependencies between feature modules.

---

## Project Structure

Detailed module configuration tables and Mermaid dependency diagrams are defined in the repository's **[README.md](./README.md#module-configuration)**.
To prevent duplicate management of information, system architecture and module structure details are centralized (SSoT) in `README.md`.

### Dependency Rules

- **Upper layers can depend on lower layers.**
- **Minimize dependencies between layers at the same level.**
- **Avoid direct dependencies between feature modules.**
- **Implement screen transitions using Compose Navigation.**
- **Leverage the Destinations library.**

Refer to [README.md](./README.md) for the detailed module dependency diagram.

### Common Development Tasks

#### Adding a New Feature

1. Create a feature module in the `feature/` directory.
2. Define domain models in `domain/model/`.
3. Implement use cases in `domain/usecase/`.
4. Add data layer components in `data/`.
5. Always run `./gradlew detektAll` after making changes.

#### Modifying Existing Features

1. Check dependencies in the module dependency diagram in README.md.
2. Update corresponding test files using the same package structure.
3. Run lint and test commands for the affected modules.
4. Verify compatibility for both Android and JVM.

#### Database Modifications

1. Room database files are located in `data/database/`.
2. Always create a migration script for schema changes.
3. Test migrations with `./gradlew :data:database:test`.
4. Update the database version in configuration.

### Platform-Specific Considerations

#### Android

- Account for Android Lifecycle in all implementations.
- Use Material3 Adaptive components.
- Adhere to Android resource naming conventions with appropriate modifiers.
- Ensure proper lifecycle management of Activities and Fragments.

#### iOS

- Use the expect/actual pattern for platform-specific implementations.
- Consider the iOS Human Interface Guidelines.
- Ensure all expect declarations have corresponding actual implementations.

#### JVM

- Consider desktop-specific UI patterns (menu bars, keyboard shortcuts).
- Implement appropriate window management.
- Test desktop-specific features such as filesystem access.

---

## Release Process

### Automated Release Flow

ComicViewer employs a fully automated release process.

#### 1. Release Drafter

`.github/workflows/release-drafter.yml` automatically performs the following:

- Creates/updates a draft release upon pushing to the main branch.
- Automatically generates release notes from merged Pull Requests.
- Assigns appropriate labels to Pull Requests.

#### 2. Release Workflow

`.github/workflows/release.yml` performs the following:

- Executes automatically when a release is published.
- Quality checks (Detekt, Lint, Test).
- Parallel builds for Android and JVM releases.
- Uploads AAB to Google Play Console (Internal App Sharing).
- Uploads artifacts to GitHub Release.
- Sends Discord notifications.

### Version Control & Versioning

#### Automated Version Calculation

- **versionName**: Retrieved from Git tags (e.g., `v0.1.0-beta.1`, `v0.1.0`).
- **versionCode**: Automatically calculated from versionName.

#### versionCode Formula

- **Official Release**: `(major * 10000 + minor * 100 + patch) * 100 + 99`
- **Beta Release**: `(major * 10000 + minor * 100 + patch) * 100 + beta_number`

**Examples:**

- `v0.1.0-beta.1` ➔ versionCode: `10001`
- `v0.1.0-beta.2` ➔ versionCode: `10002`
- `v0.1.0` ➔ versionCode: `10099`

### Release Procedure

1. **Development & PR Creation**
2. **Create an appropriate Git tag** (e.g., `v0.1.0-beta.1`).
3. **Verify and edit the draft release.**
4. **Publish the release on GitHub.**
5. **Automated builds and distribution will execute.**

For details, refer to [docs/release-automation.md](./docs/release-automation.md).

---

## Troubleshooting

### Detekt Failures

```bash
# Automatically fix code formatting issues
./gradlew detektFormat
```

**Common issues:**

- **Missing trailing commas**: Add a comma after the last element/parameter of collections and functions.
- **Import ordering**: Reorder imports using IDE tools or detekt automatic fixes.
- **Complex functions**: Break down functions exceeding complexity thresholds.

### Android Lint Failures

**Common issues:**

- **API Level Issues**: Check compatibility between minimum SDK (30) and target SDK (37).
- **Resource Naming**: Adhere to Android naming conventions for drawables, strings, and layouts.
- **Hardcoded Strings**: Extract strings to appropriate resources in the values folder.
- **Lifecycle Issues**: Ensure proper lifecycle management in Activities and Fragments.

### Multiplatform Issues

**Common issues:**

- **Expect/Actual Mismatch**: Ensure all expect declarations have corresponding actual implementations.
- **Platform-Specific APIs**: Use appropriate expect/actual patterns for platform differences.
- **Dependency Conflicts**: Check the Version Catalog for compatible multi-platform library versions.
- **Build Variant Issues**: Test changes across both Android and JVM targets.

### Module Dependency Issues

**Common issues:**

- **Circular Dependencies**: Refer to the module dependency diagram in README.md.
- **API Changes**: Update all dependent modules when changing public APIs.
- **Version Conflicts**: Use Version Catalog to maintain consistent dependency versions.
- **Missing Dependencies**: Add required dependencies to the source sets of all affected platforms.

### Performance Issues

**Common issues:**

- **Build Times**: Use Gradle daemon, configuration cache, and parallel builds (enabled by default).
- **Memory Errors**: If build fails with OOM, increase heap size in gradle.properties.
- **Cache Issues**: Clear the build cache with `./gradlew clean` if unexpected behavior occurs.
- **Network Timeouts**: Check corporate proxy settings for repository access.

### Build Environment Issues

#### Network Requirements

- Access to Google Maven repository (dl.google.com) is required.
- Builds will fail with "Plugin not found" errors in restricted environments.
- Corporate environments may require proxy configuration.

#### Alternative Verification in Restricted Environments

If network access is restricted:

- Review code changes manually against existing patterns in the codebase.
- Use locally available static analysis tools.
- Focus on consistent code style matching existing files.
- Test logic changes in isolation as much as possible.
- Validate against module dependency rules shown in README.md.

#### Common Build Failures

- **Network Connectivity**: Most common issue - check access to Google/Maven repositories.
- **Java Version Mismatch**: Ensure Java 21 is active rather than Java 17 or other versions.
- **Missing Android SDK**: Install Android SDK including API 37 and build tools.
- **Memory Issues**: Build requires sufficient memory (4GB+ recommended in gradle.properties).
- **Corrupted Gradle Cache**: Run `./gradlew clean` if the build behaves unexpectedly.

---

## References & Resources

### Official Documentation

- [Android Developers](https://developer.android.com/) - Official guidelines for Android development
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Compose development guide
- [Kotlin Multiplatform](https://kotlinlang.org/lp/multiplatform/) - KMP official documentation
- [Material Design 3](https://m3.material.io/) - Material3 design system

### Project-Specific Documentation

- [README.md](./README.md) - Project overview and module dependency diagram
- [docs/release-automation.md](./docs/release-automation.md) - Detailed release process
- [docs/screen_transition.svg](./docs/screen_transition.svg) - Screen transition diagram
- [.github/copilot-instructions.md](./.github/copilot-instructions.md) - General instructions for GitHub Copilot (What)

### Configuration Files

- `gradle/libs.versions.toml` - Centralized dependency versions
- `gradle.properties` - Gradle and build configuration
- `settings.gradle.kts` - Multi-module project settings

---
