package comicviewer.primitive

val kotlinPluginIds = listOf(
    "org.jetbrains.kotlin.jvm",
    "org.jetbrains.kotlin.android",
    "org.jetbrains.kotlin.multiplatform"
)

if (kotlinPluginIds.any(pluginManager::hasPlugin)) {
    plugins {
        dev.zacsweers.metro
    }
    configure<dev.zacsweers.metro.gradle.MetroPluginExtension> {
        contributesAsInject = true
    }
}
