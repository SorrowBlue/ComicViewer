plugins {
    alias(libs.plugins.comicviewer.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.metro)
    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "com.sorrowblue.comicviewer.app"
    defaultConfig {
        applicationId = "com.sorrowblue.comicviewer"
        targetSdk = libs.versions.targetSdk.get().toInt()
        // versionCode calculated from versionName in androidComponents block
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    androidResources {
        generateLocaleConfig = true
    }
    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.findByName("debug")
        }
        val release by getting {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.findByName("release")
        }
        create("prerelease") {
            initWith(release)
            applicationIdSuffix = ".prerelease"
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.findByName("release")
            matchingFallbacks += listOf("release")
        }
        create("internal") {
            initWith(release)
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.findByName("release")
            matchingFallbacks += listOf("release")
        }
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources.excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
    }
    lint {
        val androidLintCheckReleaseBuilds: Boolean? by project
        checkReleaseBuilds = androidLintCheckReleaseBuilds ?: true
        abortOnError = true
    }
}

dependencies {
    implementation(projects.app.share)
    implementation(projects.feature.book)
    implementation(projects.feature.settings.info)
    implementation(projects.framework.ui)

    implementation(libs.androidx.activityCompose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.coreSplashscreen)
    implementation(libs.androidx.lifecycleCommon)
    implementation(libs.androidx.navigation3UI)
    implementation(libs.compose.ui)
    implementation(libs.metro.android)

    debugImplementation(projects.domain.usecase)
    debugImplementation(projects.feature.bookshelf.edit)
    debugImplementation(libs.kotlinx.serializationJson)

    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation("androidx.compose.ui:ui-test:${libs.versions.compose.ui.get()}")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${libs.versions.compose.ui.get()}")
    debugImplementation("androidx.compose.ui:ui-test-manifest:${libs.versions.compose.ui.get()}")
    debugImplementation("androidx.compose.ui:ui-tooling:${libs.versions.compose.ui.get()}")
}

val gitTagProvider = providers.of(GitTagValueSource::class) {}
androidComponents {
    onVariants(selector().all()) { variant ->
        variant.outputs.forEach { output ->
            val versionName = gitTagProvider.orElse("0.0.0").get()
            val versionCode = calculateVersionCode(versionName)
            output.versionName.set(versionName)
            output.versionCode.set(versionCode)
            logger.lifecycle("${variant.name} versionName=$versionName, versionCode=$versionCode")
        }
    }
}

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
        val versionParts = withoutPrefix.split("-")
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
