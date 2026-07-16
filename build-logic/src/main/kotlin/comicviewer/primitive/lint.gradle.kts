/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package comicviewer.primitive

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.Lint
import com.sorrowblue.comicviewer.libs
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

when {
    pluginManager.hasPlugin(libs.plugins.androidApplication) -> {
        configure<ApplicationExtension> {
            lint {
                configure()
            }
        }
    }

    pluginManager.hasPlugin(libs.plugins.androidLibrary) -> {
        configure<LibraryExtension> {
            lint {
                configure()
            }
        }
    }

    pluginManager.hasPlugin(libs.plugins.androidMultiplatform) -> {
        configure<KotlinMultiplatformExtension> {
            configure<KotlinMultiplatformAndroidLibraryExtension> {
                lint {
                    configure()
                }
            }
        }
    }
}

private fun Lint.configure() {
    checkAllWarnings = true
    checkDependencies = true
    disable += listOf(
        "NewerVersionAvailable",
        "GradleDependency",
        "AppLinksAutoVerify",
    )
    baseline = rootProject.file("config/lint-baseline.xml")
}

private fun PluginManager.hasPlugin(provider: Provider<PluginDependency>): Boolean =
    hasPlugin(provider.get().pluginId)
