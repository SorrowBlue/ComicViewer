package com.sorrowblue.comicviewer

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project

internal fun Project.configureAndroidCompose(commonExtension: CommonExtension<*, *, *, *, *, *>) {

    commonExtension.buildFeatures.compose = true

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
