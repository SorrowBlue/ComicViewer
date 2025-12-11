package comicviewer.convention

import com.sorrowblue.comicviewer.libs

plugins {
    `kotlin-multiplatform`
    org.jetbrains.compose
    `kotlin-composecompiler`
    this.id("org.jetbrains.kotlin.plugin.serialization")
//    kotlin("plugin.serialization")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.componentsResources)
            implementation(libs.compose.preview)
            implementation(libs.compose.uiBackhandler)
            implementation(libs.kotlinx.collectionsImmutable)
            implementation(libs.androidx.lifecycleCommon)
            implementation(libs.rin)

            // Adaptive
            implementation(libs.androidx.windowCore)
            implementation(libs.compose.material3)
            implementation(libs.compose.material3Adaptive)
            implementation(libs.compose.material3AdaptiveLayout)
            implementation(libs.compose.material3AdaptiveNavigation3)
            implementation(libs.compose.material3AdaptiveNavigationSuite)

            // Navigation
            implementation(libs.androidx.lifecycleCompose)
            implementation(libs.androidx.lifecycleViewmodelNavigation3)
            implementation(libs.androidx.navigation3UI)
            implementation(libs.androidx.navigation3Runtime)
            implementation(libs.androidx.navigationeventCompose)
            implementation(libs.kotlinx.serializationCore)
            implementation(libs.navigation3.resultstate)

            // Paging
            implementation(libs.androidx.pagingCompose)
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
