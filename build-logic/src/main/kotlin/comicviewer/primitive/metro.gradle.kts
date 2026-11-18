/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package comicviewer.primitive

import dev.zacsweers.metro.gradle.DelicateMetroGradleApi
import dev.zacsweers.metro.gradle.ExperimentalMetroGradleApi

val kotlinPluginIds = listOf(
    "com.android.application",
    "org.jetbrains.kotlin.jvm",
    "org.jetbrains.kotlin.android",
    "org.jetbrains.kotlin.multiplatform",
)

if (kotlinPluginIds.any(pluginManager::hasPlugin)) {
    plugins {
        dev.zacsweers.metro
    }
    configure<dev.zacsweers.metro.gradle.MetroPluginExtension> {
        generateContributionProviders = true
        @OptIn(DelicateMetroGradleApi::class)
        reportsDestination.set(layout.buildDirectory.dir("reports/metro"))
        @OptIn(ExperimentalMetroGradleApi::class)
        enableDerivedBindings.set(true)
    }
}
