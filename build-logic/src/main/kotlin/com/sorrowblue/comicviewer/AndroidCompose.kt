package com.sorrowblue.comicviewer

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

internal fun Project.configureAndroidCompose() {
    configure<ComposeCompilerGradlePluginExtension> {
        enableStrongSkippingMode.set(true)
    }
    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask::class.java) {
        compilerOptions {
            if (project.findProperty("composeCompilerMetrics") == "true") {
                freeCompilerArgs.addAll(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=${
                        project.layout.buildDirectory.file(
                            "compose"
                        ).get().asFile.absolutePath
                    }"
                )
            }
            if (project.findProperty("composeCompilerReports") == "true") {
                freeCompilerArgs.addAll(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${
                        project.layout.buildDirectory.file(
                            "compose"
                        ).get().asFile.absolutePath
                    }"
                )
            }
        }
    }
}
