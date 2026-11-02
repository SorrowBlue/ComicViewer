/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package comicviewer.convention

import com.github.skydoves.navgraph.gradle.RenderBackend
import com.sorrowblue.comicviewer.libs

plugins {
    id("comicviewer.convention.multiplatform-library")
    id("comicviewer.convention.multiplatform-compose")
    com.github.skydoves.navgraph
    id("comicviewer.convention.navigation-library")
}

tasks.configureEach {
    if (name == "renderNavGraphGalleryLayoutlib" || name == "renderNavGraphLayoutlib") {
        // navgraphのlayoutlib描画がandroidAppのマージ済みassetsを参照するため、実行順依存を明示する
        dependsOn(":app:androidApp:mergeDebugAssets")
    }
}

kotlin {
    android {
        androidResources.enable = true
        withHostTest {
            isIncludeAndroidResources = true
        }
    }
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3ExpressiveApi",
            "-opt-in=androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi",
        )
    }
    sourceSets {
        commonMain.dependencies {
            implementation(project(":framework:designsystem"))
            implementation(project(":framework:ui"))
            implementation(project(":framework:ui:preview"))
            implementation(project(":domain:usecase"))

            implementation(libs.compose.componentsResources)
            implementation(libs.compose.preview)

            // Adaptive
            implementation(libs.compose.material3)
            implementation(libs.compose.material3AdaptiveLayout)
            implementation(libs.compose.material3AdaptiveNavigation3)

            // Navigation
            implementation(libs.androidx.lifecycleCompose)
            implementation(libs.androidx.navigation3UI)
            implementation(libs.kotlinx.serializationCore)

            implementation(libs.metro.viewmodelCompose)
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

ksp {
    arg("navgraph.annotatedOnly", "true")
}

navgraph {
    renderBackend.set(RenderBackend.LAYOUTLIB)
    galleryRenderBackend.set(RenderBackend.LAYOUTLIB)
}
