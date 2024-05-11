package com.sorrowblue.comicviewer

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        composeOptions {
//            logger.lifecycle("kotlinCompilerExtensionVersion=$kotlinCompilerExtensionVersion")
//            kotlinCompilerExtensionVersion = "1.5.13-dev-k2.0.0-RC1-50f08dfa4b4"
        }
    }

    configure<ComposeCompilerGradlePluginExtension> {
        enableStrongSkippingMode.set(true)
    }
}
