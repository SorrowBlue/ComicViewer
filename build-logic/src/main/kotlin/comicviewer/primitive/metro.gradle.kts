/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package comicviewer.primitive

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
        @Suppress("OPT_IN_USAGE")
        generateContributionProviders = true
        @Suppress("OPT_IN_USAGE")
        reportsDestination.set(layout.buildDirectory.dir("reports/metro"))
    }
}
