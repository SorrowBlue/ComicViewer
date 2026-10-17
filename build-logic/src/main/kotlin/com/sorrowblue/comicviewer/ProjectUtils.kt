/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.the
import org.gradle.plugin.use.PluginDependency

val Project.libs: LibrariesForLibs
    get() = the<LibrariesForLibs>()

fun PluginManager.hasPlugin(provider: Provider<PluginDependency>): Boolean =
    hasPlugin(provider.get().pluginId)

private val nonDiModulePaths = setOf(
    ":core:logger",
    ":konture-test",
    ":framework:designsystem",
    ":framework:navigation:processor",
    ":framework:test",
    ":framework:ui:preview",
    ":framework:permission",
    ":feature:file:nav",
)

/**
 * Subprojects that contribute to the Metro dependency graph (`AppScope`).
 */
val Project.diSubprojects: List<Project>
    get() = rootProject.subprojects.filter { subproject ->
        subproject.path != path &&
            subproject.path !in nonDiModulePaths &&
            (
                !subproject.path.startsWith(
                    ":app:",
                ) || subproject.path == ":app:share" || subproject.path == ":app:sync"
                ) &&
            subproject.projectDir.resolve("src").exists()
    }
