package comicviewer.convention

import com.sorrowblue.comicviewer.libs

plugins {
    org.jetbrains.kotlin.multiplatform
    org.jetbrains.compose
    org.jetbrains.kotlin.plugin.compose
    org.jetbrains.kotlin.plugin.serialization
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.componentsResources)
            implementation(libs.compose.preview)
        }
    }

    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3ExpressiveApi",
            "-opt-in=androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi",
        )
    }
}

composeCompiler {
    val composeCompilerReports: String? by project
    if (composeCompilerReports.toBoolean()) {
        reportsDestination.set(layout.buildDirectory.dir("compose_compiler"))
        metricsDestination.set(layout.buildDirectory.dir("compose_compiler"))
    }
}

dependencies {
    if (pluginManager.hasPlugin(libs.plugins.androidMultiplatform)) {
        add("androidRuntimeClasspath", libs.compose.uiTooling)
    }
}

private fun PluginManager.hasPlugin(provider: Provider<PluginDependency>): Boolean =
    hasPlugin(provider.get().pluginId)
