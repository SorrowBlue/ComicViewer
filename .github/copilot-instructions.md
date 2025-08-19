# ComicViewer Kotlin Multiplatform

ComicViewer is a Kotlin Multiplatform comic viewer application targeting Android, iOS, and Desktop platforms using Jetpack Compose. The project uses a modular architecture with feature/domain/data layer separation.

Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.

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
- **Detekt analysis**: `./gradlew detektAll` -- takes 10-15 minutes. NEVER CANCEL.
- **Android Lint**: `./gradlew lintDebug` -- takes 10-15 minutes. NEVER CANCEL.
- **Version catalog check**: `./gradlew versionCatalogLint` -- takes 2-3 minutes.
- **Format code**: `./gradlew detektFormat` -- formats code to match style guide.

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

## Build Environment Issues

### Network Requirements
- **CRITICAL**: Requires access to Google Maven repository (dl.google.com)
- If builds fail with "Plugin not found" errors, check network connectivity to Google's repositories
- Proxy configurations may be needed in corporate environments

### Common Build Failures
- **Java version mismatch**: Ensure Java 21 is active, not Java 17 or other versions
- **Android SDK missing**: Install Android SDK with API 36 and build tools
- **Memory issues**: Build requires significant memory (4GB+ recommended in gradle.properties)
- **Network timeouts**: Increase network timeout in gradle-wrapper.properties if needed

### CI/CD Integration
- GitHub Actions workflows are in `.github/workflows/`
- Main workflow: `lint-test-build.yml` runs full validation
- Release automation documented in `docs/release-automation.md`
- Always ensure local builds pass before pushing to avoid CI failures

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