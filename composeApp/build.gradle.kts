import com.android.build.api.variant.VariantOutput
import com.sorrowblue.comicviewer.ComicBuildType
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.application)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.compose)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            binaryOption("bundleId", "com.sorrowblue.comicviewer.app")
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.framework.designsystem)
            implementation(projects.framework.ui)
            implementation(projects.data.di)
            implementation(projects.domain.usecase)
            implementation(projects.feature.authentication)
            implementation(projects.feature.bookshelf)
            implementation(projects.feature.bookshelf.info)
            implementation(projects.feature.book)
            implementation(projects.feature.readlater)
            implementation(projects.feature.collection)
            implementation(projects.feature.search)
            implementation(projects.feature.tutorial)
            implementation(projects.feature.folder)
            implementation(projects.feature.settings)
            implementation(projects.feature.settings.info)
            implementation(projects.feature.history)

            // Di
            implementation(libs.koin.composeViewModel)

            implementation(libs.kotlinx.serialization.json)
        }


        commonTest {
            dependencies {
                implementation(projects.framework.test)
                implementation(libs.kotlin.test)
                implementation(libs.koin.test)
            }
        }

        androidMain.dependencies {
            implementation(projects.framework.notification)

            implementation(libs.androidx.core.splashscreen)
            implementation(libs.koin.androidxCompose)
            implementation(libs.koin.androidxStartup)
            implementation(libs.koin.androidxWorkmanager)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.google.android.play.feature.delivery.ktx)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }

        androidUnitTest {
            dependencies {
                implementation(projects.data.storage.client)
            }
        }

        desktopTest {
            dependencies {
                implementation(projects.data.storage.client)
            }
        }
    }
}

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

    buildTypes {
        val release by getting {
            applicationIdSuffix = ComicBuildType.RELEASE.applicationIdSuffix
            isMinifyEnabled = ComicBuildType.RELEASE.isMinifyEnabled
            isShrinkResources = ComicBuildType.RELEASE.isShrinkResources
            signingConfig = signingConfigs.findByName(name)
        }
        prerelease {
            initWith(release)
            applicationIdSuffix = ComicBuildType.PRERELEASE.applicationIdSuffix
            isMinifyEnabled = ComicBuildType.PRERELEASE.isMinifyEnabled
            isShrinkResources = ComicBuildType.PRERELEASE.isShrinkResources
            signingConfig = signingConfigs.findByName(name)
        }
        internal {
            initWith(release)
            applicationIdSuffix = ComicBuildType.INTERNAL.applicationIdSuffix
            isMinifyEnabled = ComicBuildType.INTERNAL.isMinifyEnabled
            isShrinkResources = ComicBuildType.INTERNAL.isShrinkResources
            signingConfig = signingConfigs.findByName(name)
        }
        debug {
            applicationIdSuffix = ComicBuildType.DEBUG.applicationIdSuffix
            isMinifyEnabled = ComicBuildType.DEBUG.isMinifyEnabled
            isShrinkResources = ComicBuildType.DEBUG.isShrinkResources
            signingConfig = signingConfigs.findByName(name)
        }
    }

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

compose.desktop {
    application {
        mainClass = "com.sorrowblue.comicviewer.app.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.sorrowblue.comicviewer.app"
            packageVersion = "1.0.0"
        }
        jvmArgs("-Dsun.stdout.encoding=UTF-8", "-Dsun.stderr.encoding=UTF-8")
    }
}

val gitTagProvider = providers.of(GitTagValueSource::class) {}

androidComponents {
    onVariants(selector().all()) { variant ->
        variant.outputs.forEach { output: VariantOutput ->
            val vn = gitTagProvider.orElse("0.0.0").get()
            val versionCode = calculateVersionCode(vn)
            
            output.versionName.set(vn)
            output.versionCode.set(versionCode)
            
            logger.lifecycle("${variant.name} versionName=$vn, versionCode=$versionCode")
        }
    }
}

/**
 * Calculate versionCode from versionName.
 * Converts semantic version (e.g., "1.2.3") to integer.
 * Format: major * 10000 + minor * 100 + patch
 * Falls back to 1 for invalid versions.
 */
fun calculateVersionCode(versionName: String): Int {
    return try {
        // Remove any non-numeric suffixes (e.g., "-beta1", "-SNAPSHOT")
        val cleanVersion = versionName.split("-")[0]
        val parts = cleanVersion.split(".")
        
        if (parts.size >= 3) {
            val major = parts[0].toIntOrNull() ?: 0
            val minor = parts[1].toIntOrNull() ?: 0
            val patch = parts[2].toIntOrNull() ?: 0
            
            // Ensure values are within reasonable bounds
            val boundedMajor = major.coerceIn(0, 99)
            val boundedMinor = minor.coerceIn(0, 99)
            val boundedPatch = patch.coerceIn(0, 99)
            
            boundedMajor * 10000 + boundedMinor * 100 + boundedPatch
        } else {
            logger.warn("Invalid version format: $versionName, using versionCode 1")
            1
        }
    } catch (e: Exception) {
        logger.warn("Error parsing version $versionName: ${e.message}, using versionCode 1")
        1
    }
}
