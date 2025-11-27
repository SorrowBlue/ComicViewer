import com.android.build.api.variant.VariantOutput
//import com.sorrowblue.comicviewer.ComicBuildType
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
//    alias(libs.plugins.comicviewer.kotlinMultiplatform.application)
//    alias(libs.plugins.comicviewer.multiplatformCompose)
//    alias(libs.plugins.comicviewer.di)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.composeHotReload)
}

//kotlin {
//    listOf(
//        iosX64(),
//        iosArm64(),
//        iosSimulatorArm64()
//    ).forEach { iosTarget ->
//        iosTarget.binaries.framework {
//            baseName = "ComposeApp"
//            isStatic = true
//            binaryOption("bundleId", "com.sorrowblue.comicviewer.app")
//        }
//    }
//
//    sourceSets {
//        commonMain.dependencies {
//            implementation(projects.aggregation)
//            implementation(projects.feature.settings.info)
//            implementation(projects.framework.designsystem)
//            implementation(projects.framework.ui)
//        }
//
//        androidMain.dependencies {
//            implementation(libs.androidx.appcompat)
//            implementation(libs.androidx.core.splashscreen)
//            implementation(projects.feature.book)
//            implementation(libs.kotlinx.coroutines.android)
//        }
//
//        desktopMain.dependencies {
//            implementation(compose.desktop.currentOs)
//            implementation(libs.kotlinx.coroutines.swing)
//        }
//    }
//}

android {
    namespace = "com.sorrowblue.comicviewer.app"
    defaultConfig {
        applicationId = "com.sorrowblue.comicviewer"
        targetSdk = libs.versions.targetSdk.get().toInt()
        // versionCode calculated from versionName in androidComponents block
    }
    androidResources {
        @Suppress("UnstableApiUsage")
        generateLocaleConfig = true
    }

//    buildTypes {
//        val release by getting {
//            applicationIdSuffix = ComicBuildType.RELEASE.applicationIdSuffix
//            isMinifyEnabled = ComicBuildType.RELEASE.isMinifyEnabled
//            isShrinkResources = ComicBuildType.RELEASE.isShrinkResources
//            signingConfig = signingConfigs.findByName(name)
//        }
//        prerelease {
//            initWith(release)
//            applicationIdSuffix = ComicBuildType.PRERELEASE.applicationIdSuffix
//            isMinifyEnabled = ComicBuildType.PRERELEASE.isMinifyEnabled
//            isShrinkResources = ComicBuildType.PRERELEASE.isShrinkResources
//            signingConfig = signingConfigs.findByName(name)
//        }
//        internal {
//            initWith(release)
//            applicationIdSuffix = ComicBuildType.INTERNAL.applicationIdSuffix
//            isMinifyEnabled = ComicBuildType.INTERNAL.isMinifyEnabled
//            isShrinkResources = ComicBuildType.INTERNAL.isShrinkResources
//            signingConfig = signingConfigs.findByName(name)
//        }
//        debug {
//            applicationIdSuffix = ComicBuildType.DEBUG.applicationIdSuffix
//            isMinifyEnabled = ComicBuildType.DEBUG.isMinifyEnabled
//            isShrinkResources = ComicBuildType.DEBUG.isShrinkResources
//            signingConfig = signingConfigs.findByName(name)
//        }
//    }

    buildFeatures.buildConfig = true

    packaging {
        resources.excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
    }

    sourceSets {
        getByName("main").res.srcDirs("src/commonMain/composeResources")
    }

    lint {
        val androidLintCheckReleaseBuilds: Boolean? by project
        checkReleaseBuilds = androidLintCheckReleaseBuilds ?: true
        abortOnError = true
    }
}

//val gitTagProvider = providers.of(GitTagValueSource::class) {}
//
//compose.desktop {
//    application {
//        mainClass = "com.sorrowblue.comicviewer.app.MainKt"
//        nativeDistributions {
//            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
//            packageName = "com.sorrowblue.comicviewer.app"
//            packageVersion = extractPackageVersion(gitTagProvider.orElse("1.0.0").get())
//        }
//        jvmArgs("-Dsun.stdout.encoding=UTF-8", "-Dsun.stderr.encoding=UTF-8")
//    }
//}

/**
 * Extract package version from git tag.
 * Converts git tags (e.g., "v1.2.3" or "1.2.3-beta.1") to package version format.
 * Format: [MAJOR].[MINOR].[BUILD]
 * - v1.2.3 → 1.2.3
 * - v1.2.3-beta.1 → 1.2.3
 * - 1.2.3-beta.1-1-gf13fe → 1.2.3
 * Falls back to "1.0.0" for invalid versions.
 */
fun extractPackageVersion(versionName: String): String {
    return try {
        // Remove 'v' prefix if present
        val withoutPrefix = if (versionName.startsWith("v")) {
            versionName.substring(1)
        } else {
            versionName
        }
        
        // Split version and suffix (e.g., "1.2.3-beta.1" -> ["1.2.3", "beta.1"])
        val versionParts = withoutPrefix.split("-", limit = 2)
        val baseVersion = versionParts[0]
        
        // Parse semantic version parts and validate format
        val parts = baseVersion.split(".")
        if (parts.size >= 3) {
            val major = parts[0].toIntOrNull()?.coerceAtLeast(1) ?: 1
            val minor = parts[1].toIntOrNull() ?: 0
            val build = parts[2].toIntOrNull() ?: 0
            "$major.$minor.$build"
        } else {
            logger.warn("Invalid version format: $versionName, using packageVersion 1.0.0")
            "1.0.0"
        }
    } catch (e: Exception) {
        logger.warn("Error parsing version $versionName: ${e.message}, using packageVersion 1.0.0")
        "1.0.0"
    }
}

//androidComponents {
//    onVariants(selector().all()) { variant ->
//        variant.outputs.forEach { output: VariantOutput ->
//            val vn = gitTagProvider.orElse("0.0.0").get()
//            val versionCode = calculateVersionCode(vn)
//
//            output.versionName.set(vn)
//            output.versionCode.set(versionCode)
//
//            logger.lifecycle("${variant.name} versionName=$vn, versionCode=$versionCode")
//        }
//    }
//}

/**
 * Calculate versionCode from versionName.
 * Converts semantic version (e.g., "v1.2.3" or "v1.2.3-beta.1") to integer.
 * Format: 
 * - Final releases: (major * 10000 + minor * 100 + patch) * 100 + 99
 * - Beta releases: (major * 10000 + minor * 100 + patch) * 100 + beta_number
 * Falls back to 1 for invalid versions.
 */
fun calculateVersionCode(versionName: String): Int {
    return try {
        // Remove 'v' prefix if present
        val withoutPrefix = if (versionName.startsWith("v")) {
            versionName.substring(1)
        } else {
            versionName
        }
        
        // Split version and suffix (e.g., "1.2.3-beta.1" -> ["1.2.3", "beta.1"])
        val versionParts = withoutPrefix.split("-", limit = 2)
        val baseVersion = versionParts[0]
        val suffix = if (versionParts.size > 1) versionParts[1] else null
        
        // Parse semantic version parts
        val parts = baseVersion.split(".")
        if (parts.size >= 3) {
            val major = parts[0].toIntOrNull() ?: 0
            val minor = parts[1].toIntOrNull() ?: 0
            val patch = parts[2].toIntOrNull() ?: 0
            
            // Ensure values are within reasonable bounds
            val boundedMajor = major.coerceIn(0, 99)
            val boundedMinor = minor.coerceIn(0, 99)
            val boundedPatch = patch.coerceIn(0, 99)
            
            val baseVersionCode = boundedMajor * 10000 + boundedMinor * 100 + boundedPatch
            
            // Handle beta versions (e.g., "beta.1", "beta.2")
            if (suffix != null && suffix.startsWith("beta.")) {
                val betaNumber = suffix.substring(5).toIntOrNull() ?: 1
                val boundedBeta = betaNumber.coerceIn(1, 98)
                baseVersionCode * 100 + boundedBeta
            } else {
                // Final release - use 99 as suffix to ensure it's higher than any beta
                baseVersionCode * 100 + 99
            }
        } else {
            logger.warn("Invalid version format: $versionName, using versionCode 1")
            1
        }
    } catch (e: Exception) {
        logger.warn("Error parsing version $versionName: ${e.message}, using versionCode 1")
        1
    }
}
