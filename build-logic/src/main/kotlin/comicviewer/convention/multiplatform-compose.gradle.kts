/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package comicviewer.convention

import com.sorrowblue.comicviewer.configureKotlin
import com.sorrowblue.comicviewer.hasPlugin
import com.sorrowblue.comicviewer.libs
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    org.jetbrains.kotlin.multiplatform
    org.jetbrains.compose
    org.jetbrains.kotlin.plugin.compose
    org.jetbrains.kotlin.plugin.serialization
}

kotlin {

    configureKotlin<KotlinMultiplatformExtension>()
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3ExpressiveApi",
            "-opt-in=androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi",
        )
    }
}

val enableComposeCompilerReports = providers.gradleProperty("enableComposeCompilerReports")
    .map(String::toBoolean)
    .orElse(false)

composeCompiler {
    reportsDestination.set(
        enableComposeCompilerReports.flatMap { enabled ->
            if (enabled) {
                layout.buildDirectory.dir("compose_compiler/reports")
            } else {
                objects.directoryProperty()
            }
        },
    )
    metricsDestination.set(
        enableComposeCompilerReports.flatMap { enabled ->
            if (enabled) {
                layout.buildDirectory.dir("compose_compiler/metrics")
            } else {
                objects.directoryProperty()
            }
        },
    )
    val stabilityConfigFile = rootProject.layout.projectDirectory.file(
        "config/compose/compose_compiler_config.conf",
    )
    if (stabilityConfigFile.asFile.exists()) {
        stabilityConfigurationFiles.add(stabilityConfigFile)
    }
}

dependencies {
    if (pluginManager.hasPlugin(libs.plugins.androidMultiplatform)) {
        add("androidRuntimeClasspath", libs.compose.uiTooling)
    }
}
