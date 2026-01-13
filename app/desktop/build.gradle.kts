    import com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension
import com.sorrowblue.comicviewer.configureKotlin
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.comicviewer.multiplatformCompose)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.comicviewer.di)
    alias(libs.plugins.comicviewer.primitive.licensee)
    alias(libs.plugins.comicviewer.primitive.aboutlibraries)
}

configure<AboutLibrariesExtension> {
    export {
        outputFile.set(file("src/jvmMain/composeResources/files/aboutlibraries.json"))
    }
}
configureKotlin<KotlinMultiplatformExtension>()

kotlin {
    jvm()
    applyDefaultHierarchyTemplate()
    sourceSets {
        jvmMain.dependencies {
            implementation(projects.app.share)
            implementation(projects.feature.settings.info)
            implementation(projects.framework.common)
            implementation(projects.framework.designsystem)
            implementation(projects.framework.ui)

            implementation(compose.desktop.currentOs)

            implementation(libs.androidx.lifecycleViewmodelCompose)
            implementation(libs.compose.material3)
            implementation(libs.compose.componentsAnimatedimage)
        }
    }
}

val gitTagProvider = providers.of(GitTagValueSource::class) {}

compose.desktop {
    application {
        mainClass = "com.sorrowblue.comicviewer.app.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.sorrowblue.comicviewer.app"
            packageVersion = extractPackageVersion(gitTagProvider.orElse("1.0.0").get())
        }
        jvmArgs("-Dsun.stdout.encoding=UTF-8", "-Dsun.stderr.encoding=UTF-8")
    }
}

/**
 * Extract package version from git tag.
 * Converts git tags (e.g., "v1.2.3" or "1.2.3-beta.1") to package version format.
 * Format: MAJOR.MINOR.BUILD
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
