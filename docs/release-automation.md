# Release Automation

This document describes the automated release process for ComicViewer using GitHub Actions.

## Overview

The release automation workflow (`/.github/workflows/release.yml`) provides end-to-end automation for releasing both Android and Desktop versions of ComicViewer.

## Workflows

### Release Workflow
The main release workflow (`/.github/workflows/release.yml`) runs in two scenarios:

1. **Automatic**: When a release is published on GitHub
2. **Manual**: Using the "Run workflow" button in the GitHub Actions tab

### Version Bump and Release Workflow
The version bump workflow (`/.github/workflows/version-bump-release.yml`) automates the complete release process:

1. **Manual**: Using the "Run workflow" button in the GitHub Actions tab
2. **Features**:
   - Automatically increments `versionCode` in `gradle/libs.versions.toml`
   - Commits changes directly to main branch with `:rocket: bump versionCode` message
   - Finds latest draft release and publishes it appropriately
   - Determines release type (PreRelease vs Release) based on "beta" tag detection

## Process Flow

### 1. Quality Checks
Before any release artifacts are built, the following quality checks must pass:
- **Detekt**: Static code analysis for Kotlin code
- **Android Lint**: Android-specific code quality checks  
- **Version Catalog**: Validation of dependency versions
- **Unit Tests**: All project unit tests

### 2. Android Release
If quality checks pass:
- Builds release AAB (Android App Bundle) with proper code signing
- Uploads AAB to Google Play Console (Internal App Sharing)
- Stores AAB and APK artifacts for GitHub Release

### 3. Desktop Release
Parallel to Android release:
- Builds native distributions for Windows, macOS, and Linux
- Creates platform-specific packages using Compose Multiplatform
- Stores distribution artifacts for GitHub Release

### 4. Asset Upload & Notifications
After successful builds:
- Uploads all artifacts to the GitHub Release page
- Sends success notification to Discord with download links
- On failure, sends error notification to Discord

## Required Secrets

The following secrets must be configured in GitHub repository settings:

### Android Signing
- `ANDROID_STORE_FILE_BASE64`: Base64-encoded Android keystore file
- `ANDROID_STORE_PASSWORD`: Password for the keystore file
- `ANDROID_KEY_ALIAS`: Key alias within the keystore
- `ANDROID_KEY_PASSWORD`: Password for the signing key

### Google Play Console
- `GOOGLE_WORKLOAD_IDENTITY_PROVIDER`: Google Cloud workload identity provider
- `GOOGLE_SERVICE_ACCOUNT`: Google service account for Play Console access

### Discord Notifications
- `DISCORD_WEBHOOK`: Discord webhook URL for notifications

## Environment Requirements

The workflow uses the `android` environment, which should be configured with:
- Required secrets listed above
- Appropriate protection rules if desired

## Manual Release Process

### Option 1: Traditional Release Process

1. Create a new release on GitHub:
   - Go to "Releases" in the repository
   - Click "Create a new release"
   - Choose or create a tag (e.g., `v1.0.0`)
   - Add release notes
   - Click "Publish release"

2. The workflow will automatically start and:
   - Run all quality checks
   - Build release artifacts
   - Upload to Play Console
   - Upload artifacts to GitHub Release
   - Send Discord notifications

### Option 2: Automated Version Bump and Release

1. Use the "Version Bump and Release" workflow:
   - Go to "Actions" tab in the repository
   - Select "Version Bump and Release" workflow
   - Click "Run workflow"
   - Optionally enable "dry run" mode for testing

2. The workflow will automatically:
   - Increment `versionCode` in `gradle/libs.versions.toml`
   - Commit changes to main branch with `:rocket: bump versionCode`
   - Find the latest draft release
   - Determine if it should be PreRelease (contains "beta") or Release
   - Publish the draft release appropriately

## Monitoring

- Check the Actions tab for workflow progress
- Discord will receive notifications for success/failure
- Release artifacts will appear on the GitHub Release page
- Play Console upload URL will be provided in Discord notification

## Troubleshooting

If the workflow fails:
1. Check the workflow logs in the Actions tab
2. Verify all required secrets are configured
3. Ensure the `android` environment exists and has access to secrets
4. Check Discord for error notifications with direct links to logs