# ComicViewer Kotlin Multiplatform

ComicViewer is a Kotlin Multiplatform comic viewer application targeting Android, iOS, and Desktop platforms using Jetpack Compose. The project uses a modular architecture with feature/domain/data layer separation.

Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.

## Technology Stack

- **Kotlin Multiplatform**: Primary programming language
- **Jetpack Compose**: UI framework for Android, Desktop, and iOS
- **Modular Architecture**: feature/domain/data layer structure
- **Koin**: Dependency injection framework
- **Room**: Database (Android)
- **Ktor**: Network communication

## Working Effectively

### Prerequisites and Setup
- **CRITICAL**: Requires Java 21 (NOT Java 17). Set `JAVA_HOME` to Java 21 installation.
- **CRITICAL**: Requires Android SDK with API 36 (compileSdk) and minimum API 30.
- **CRITICAL**: Requires network access to Google's Maven repository (dl.google.com) for Android Gradle Plugin.
- Gradle 9.0.0 (included via wrapper)
- Uses Kotlin 2.2.10 and Compose Multiplatform

### Bootstrap and Build Commands
- **Initial setup**: Ensure Java 21 is active: `java -version` should show OpenJDK 21+
- **NEVER CANCEL builds**: Full builds take 30-45 minutes. Set timeout to 60+ minutes minimum.
- **Clean build**: `./gradlew clean build` -- takes 45+ minutes. NEVER CANCEL.
- **Android lint**: `./gradlew composeApp:lintDebug` -- takes 10-15 minutes. NEVER CANCEL.
- **Static analysis**: `./gradlew detektAndroidAll detektDesktopAll` -- takes 15-20 minutes. NEVER CANCEL.

### Testing Commands
- **All tests**: `./gradlew test` -- takes 20-30 minutes. NEVER CANCEL.
- **Unit tests only**: `./gradlew testDebugUnitTest` -- takes 10-15 minutes. NEVER CANCEL.
- **Desktop tests**: `./gradlew desktopTest` -- takes 5-10 minutes. NEVER CANCEL.

### Platform-Specific Builds
- **Android Debug**: `./gradlew composeApp:assembleDebug` -- takes 25-35 minutes. NEVER CANCEL.
- **Android Release**: `./gradlew composeApp:assembleRelease` -- takes 30-40 minutes. NEVER CANCEL.
- **Desktop**: `./gradlew composeApp:packageDistributionForCurrentOS` -- takes 20-30 minutes. NEVER CANCEL.

### Quality Assurance (MANDATORY before commits)
- **All Detekt checks**: `./gradlew detektAndroidAll detektDesktopAll detektIosAll detektMetadataAll` -- takes 15-20 minutes total. NEVER CANCEL.
- **Build-logic Detekt**: `./gradlew :build-logic:detektAll` -- takes 3-5 minutes. NEVER CANCEL.
- **Android Lint (all variants)**: 
  - Debug: `./gradlew composeApp:lintDebug` -- takes 8-12 minutes. NEVER CANCEL.
  - Internal: `./gradlew composeApp:lintInternal` -- takes 8-12 minutes. NEVER CANCEL.
  - Prerelease: `./gradlew composeApp:lintPrerelease` -- takes 8-12 minutes. NEVER CANCEL.
  - Release: `./gradlew composeApp:lintRelease` -- takes 10-15 minutes. NEVER CANCEL.
- **Version catalog check**: `./gradlew versionCatalogLint` -- takes 2-3 minutes.
- **Format code**: `./gradlew detektFormat` -- formats code to match Kotlin style guide.

### Build Verification Commands
These commands verify the project builds correctly across all targets:
- **Full clean build**: `./gradlew clean build` -- takes 45-60 minutes. NEVER CANCEL.
- **Compose app only**: `./gradlew composeApp:build` -- takes 30-40 minutes. NEVER CANCEL.
- **All modules check**: `./gradlew check` -- runs tests and validation, takes 25-35 minutes. NEVER CANCEL.

## Validation Scenarios

After making changes, ALWAYS validate with these complete scenarios:

### Android Validation
1. Build and test Android variant: `./gradlew composeApp:assembleDebug composeApp:testDebugUnitTest`
2. Run lint checks: `./gradlew composeApp:lintDebug`
3. Manually test core user flows if UI changes are made (viewing comics, navigation, settings)

### Desktop Validation  
1. Build desktop application: `./gradlew composeApp:packageDistributionForCurrentOS`
2. Test desktop-specific features (window management, file system access)
3. Verify multiplatform code works across targets

### Cross-Platform Validation
1. Run all detekt checks: `./gradlew detektAndroidAll detektDesktopAll detektIosAll`
2. Test shared domain/data modules: `./gradlew :domain:model:test :data:database:test`
3. Verify expect/actual implementations work correctly

## Coding Standards and Guidelines

### Kotlin Style Requirements
- **MANDATORY**: Follow Android's [Kotlin style guide](https://developer.android.com/kotlin/style-guide)
- **MANDATORY**: Use trailing commas in all collections, function parameters, and class declarations
- **MANDATORY**: Use camelCase for function names, PascalCase for class names
- Package structure: Always use `com.sorrowblue.comicviewer.*` prefix
- File endings: Include newline at end of files
- Import organization: Group `*` imports, then `^` imports (detekt enforced)

### Compose-Specific Rules  
- Use `@Composable` functions with named parameters
- Leverage default arguments with trailing lambda syntax
- Prefer Material3 components over custom implementations
- Use `@Preview` annotations for UI component previews
- Follow Compose naming conventions: `ScreenContent`, `ScreenState`, etc.
- Use Material3 Adaptive components for Android
- Minimize recomposition with proper state management

### Documentation Requirements
- **Public APIs**: Add KDoc comments to all public functions and classes
- **Complex logic**: Add Japanese comments for business logic explanations
- **TODOs**: Include assignee information in TODO comments
- **Architecture decisions**: Document expect/actual implementations for multiplatform code

### Testing Standards
- New features MUST include unit tests
- UI tests use Compose testing framework
- Test files follow same package structure as source files
- Mock external dependencies using test doubles
- Test multiplatform code on all target platforms

### Quality Assurance Requirements
- **Input validation**: Always implement input validation
- **Null safety**: Consider null safety in all code
- **External libraries**: Use latest stable versions
- **API communication**: Include proper error handling
- **Lifecycle awareness**: Implement proper lifecycle management for Android
- **Memory management**: Avoid memory leaks with proper lifecycle management

## Navigation and Architecture

### Key Module Structure
```
├── composeApp/           # Main application (Android/Desktop entry points)
├── feature/              # UI feature modules (screens and navigation)
│   ├── authentication/  # Login/authentication screens
│   ├── book/            # Comic book viewer and management
│   ├── bookshelf/       # Library and collection views
│   ├── favorite/        # Favorites management
│   ├── file/            # File browser and management
│   ├── folder/          # Folder navigation
│   ├── library/         # External library integrations
│   ├── readlater/       # Read later functionality
│   ├── search/          # Search and discovery
│   ├── settings/        # Application settings
│   └── tutorial/        # User onboarding
├── domain/              # Business logic layer
│   ├── model/           # Data models and entities
│   ├── service/         # Business services
│   └── usecase/         # Use case implementations
├── data/                # Data access layer
│   ├── coil/            # Image loading configuration
│   ├── database/        # Room database setup
│   ├── reader/          # File reading implementations
│   └── storage/         # Storage client implementations
└── framework/           # Shared framework components
    ├── common/          # Common utilities
    ├── designsystem/    # Design system components
    ├── notification/    # Notification handling
    └── ui/              # Common UI components
```

### Dependency Rules
- Upper layers can depend on lower layers
- Minimize dependencies between same-level layers
- Avoid direct dependencies between feature modules
- Use Compose Navigation for screen transitions
- Leverage Destinations library for navigation implementation

### Common Development Tasks

#### Adding New Features
1. Create feature module in `feature/` directory
2. Define domain models in `domain/model/`
3. Implement use cases in `domain/usecase/`
4. Add data layer components in `data/`
5. Always run detekt after changes: `./gradlew detektAll`

#### Modifying Existing Features
1. Check dependencies: Use module dependency diagram in README.md
2. Update corresponding test files in same package structure
3. Run relevant lint and test commands for affected modules
4. Always check both Android and Desktop compatibility

#### Database Changes
1. Room database files are in `data/database/`
2. Always create migration scripts for schema changes
3. Test migrations with: `./gradlew :data:database:test`
4. Update version in database configuration

## Platform-Specific Considerations

### Android
- Consider Android Lifecycle in all implementations
- Use Material3 Adaptive components
- Follow Android resource naming conventions with appropriate qualifiers
- Ensure proper Activity and Fragment lifecycle management

### iOS
- Use expect/actual patterns for platform-specific implementations
- Consider iOS Human Interface Guidelines
- Verify all expect declarations have corresponding actual implementations

### Desktop
- Consider desktop-specific UI patterns (menu bars, keyboard shortcuts)
- Implement proper window management features
- Test desktop-specific features like file system access

## Localization

- Implement string resources with multi-language support in mind
- Japanese as default language, with English support
- Use `Res.string.*` pattern for string resources
- Extract all hardcoded strings to resources

## Build Environment Issues

### Network Requirements
- **CRITICAL**: Requires access to Google Maven repository (dl.google.com)
- **CRITICAL**: Cannot access Google repositories in restricted environments - builds will fail with "Plugin not found" errors
- If working in restricted environments, request network access to:
  - `dl.google.com` (Google Maven repository)
  - `repo1.maven.org` (Maven Central)
  - `plugins.gradle.org` (Gradle Plugin Portal)
- Proxy configurations may be needed in corporate environments

### Alternative Validation in Restricted Environments
When network access is limited:
- Review code changes manually against existing patterns in the codebase
- Use static analysis tools locally (if available without network deps)
- Focus on consistent code style matching existing files
- Test logic changes in isolation where possible
- Validate against module dependency rules shown in README.md

### Common Build Failures
- **Network connectivity**: Most common issue - check access to Google/Maven repositories
- **Java version mismatch**: Ensure Java 21 is active, not Java 17 or other versions
- **Android SDK missing**: Install Android SDK with API 36 and build tools
- **Memory issues**: Build requires significant memory (4GB+ recommended in gradle.properties)
- **Gradle cache corruption**: Clear with `./gradlew clean` if builds behave unexpectedly

### CI/CD Integration
- **Main workflow**: `.github/workflows/lint-test-build.yml` runs complete validation pipeline
- **Quality gates**: All PRs must pass lint, detekt, and test phases
- **Build matrix**: Tests Debug, Internal, Prerelease, and Release variants
- **Static analysis**: Separate jobs for Android, Desktop, iOS, and Metadata detekt checks
- **Artifacts**: GitHub Actions generate SARIF reports for security scanning
- **Release automation**: Documented in `docs/release-automation.md` with automated Android/Desktop builds
- **Branch protection**: Main branch requires passing CI checks
- **Local validation**: ALWAYS run `./gradlew detektAll lintDebug test` before pushing to avoid CI failures

## Important Files and Locations

### Configuration Files
- `gradle/libs.versions.toml` - Centralized dependency versions
- `gradle.properties` - Gradle and build configuration
- `settings.gradle.kts` - Multi-module project setup
- `copilot-workspace-custom-instructions.md` - Additional coding guidelines

### Documentation
- `README.md` - Project overview and module dependency diagram
- `docs/release-automation.md` - Release process documentation
- `docs/screen_transition.svg` - UI flow diagram

### Build Scripts
- `build-logic/` - Custom Gradle plugins and build configuration
- Root `build.gradle.kts` - Project-wide build configuration
- Module-specific `build.gradle.kts` files for individual components

### Quality Assurance
- `.github/workflows/static-code-analysis.yml` - Detekt configuration and execution
- `.github/workflows/lint.yml` - Android Lint configuration
- `config/detekt/` - Detekt rule configuration (if present)

Always run complete validation scenarios before committing changes, and ensure adequate timeout values for all build and test operations.

## Reference Resources

- [Android Developers](https://developer.android.com/) - Official Android development guidelines
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Compose development guide
- [Kotlin Multiplatform](https://kotlinlang.org/lp/multiplatform/) - Official KMP documentation
- [Material Design 3](https://m3.material.io/) - Material3 design system
- Project-specific details: See `docs/` folder

## Git and CI/CD Guidelines

### Commit Message Standards
- Write commit messages in English
- Follow Conventional Commits format
- Use types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

### Branch Naming Convention
- Format: `[type]/[issue-number]-[simplified-issue-title]`
- Types:
  - `feature/`: New feature proposals and implementations
  - `enhancement/`: Improvements to existing functions
  - `refactor/`: Internal code improvements (no feature changes)
  - `fix/`: Bug fixes and unexpected behavior corrections
  - `doc/`: Documentation creation, editing, additions
  - `dependencies/`: Dependency updates
  - `chore/`: Build, CI/CD, dependency updates, etc.

### Issue and PR Language Guidelines
- **Issue titles**: Write in English
- **Issue descriptions/comments**: Write in Japanese
- **PR titles**: Write in English
- **PR descriptions/comments**: Write in Japanese
- **Conversations/Communication**: Conduct in Japanese

### Label Management
- Choose appropriate labels from `.github/labels.yml` for Issues
- PR labels are automatically assigned by ReleaseDrafter
- Do not manually assign PR labels

### Quality Gates
- Run detekt static analysis before commits: `./gradlew detektAll`
- Run Android Lint checks: `./gradlew composeApp:lintDebug`
- Automatic tests execute on pull request creation
- All PRs must pass lint, detekt, and test phases

## Troubleshooting Common Development Scenarios

### Detekt Failures
- **Format issues**: Run `./gradlew detektFormat` to auto-fix style violations
- **Trailing comma missing**: Add commas after last parameter/element in collections and functions
- **Import organization**: Rearrange imports with IDE tools or detekt auto-fix
- **Complex function**: Break down functions exceeding complexity thresholds

### Android Lint Failures  
- **API level issues**: Check minimum SDK (30) and target SDK (36) compatibility
- **Resource naming**: Follow Android naming conventions for drawables, strings, layouts
- **Hardcoded strings**: Extract strings to resources in appropriate values folders
- **Lifecycle issues**: Ensure proper lifecycle management in Activities and Fragments

### Multiplatform Issues
- **Expect/Actual mismatch**: Verify all expect declarations have corresponding actual implementations
- **Platform-specific APIs**: Use appropriate expect/actual patterns for platform differences
- **Dependency conflicts**: Check version catalog for compatible multiplatform library versions
- **Build variant issues**: Test changes on both Android and Desktop targets

### Module Dependency Issues
- **Circular dependencies**: Refer to README.md module dependency diagram
- **API changes**: Update all dependent modules when changing public APIs
- **Version conflicts**: Use version catalog to maintain consistent dependency versions
- **Missing dependencies**: Add required dependencies to all affected platform source sets

### Performance Issues
- **Build time**: Use Gradle daemon, configuration cache, and parallel builds (enabled by default)
- **Memory errors**: Increase heap size in gradle.properties if builds fail with OOM
- **Cache issues**: Clear build cache with `./gradlew clean` when experiencing odd behaviors
- **Network timeouts**: Check corporate proxy settings for repository access
- **Compose recomposition**: Write code to minimize Compose recomposition
- **Large datasets**: Use LazyColumn/LazyRow for handling large amounts of data
- **Image loading**: Use Coil library for efficient image loading