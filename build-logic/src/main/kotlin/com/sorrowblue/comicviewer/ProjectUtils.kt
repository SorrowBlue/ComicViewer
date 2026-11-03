/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.project
import org.gradle.kotlin.dsl.the
import org.gradle.plugin.use.PluginDependency

val Project.libs: LibrariesForLibs
    get() = the<LibrariesForLibs>()

fun PluginManager.hasPlugin(provider: Provider<PluginDependency>): Boolean =
    hasPlugin(provider.get().pluginId)

internal val nonDiModulePaths = setOf(
    ":core:logger",
    ":domain:repository",
    ":feature:file:nav",
    ":feature:settings:common",
    ":framework:common",
    ":framework:designsystem",
    ":framework:navigation",
    ":framework:navigation:processor",
    ":framework:permission",
    ":framework:test",
    ":framework:ui:preview",
    ":konture-test",
)

private val includeRegex = Regex("""^\s*include\("([^"]+)"\)""")

/**
 * Subprojects that contribute to the Metro dependency graph (`AppScope`).
 *
 * Uses `isolated.rootProject.projectDirectory` instead of `rootProject.subprojects`
 * to remain compatible with Gradle Isolated Projects.
 */
val Project.diSubprojects: List<ProjectDependency>
    get() {
        val rootDir = isolated.rootProject.projectDirectory
        val settingsFile = rootDir.file("settings.gradle.kts").asFile
        return settingsFile.useLines { lines ->
            lines.mapNotNull { line ->
                includeRegex.find(line)?.groupValues?.get(1)
            }.filter { modulePath ->
                modulePath != path &&
                    modulePath !in nonDiModulePaths &&
                    (
                        !modulePath.startsWith(":app:") ||
                            modulePath == ":app:share" ||
                            modulePath == ":app:sync"
                        ) &&
                    rootDir.dir(modulePath.removePrefix(":").replace(":", "/"))
                        .dir("src").asFile.exists()
            }.map { modulePath ->
                dependencies.project(path = modulePath)
            }.toList()
        }
    }
