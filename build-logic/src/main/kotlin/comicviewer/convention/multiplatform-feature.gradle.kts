package comicviewer.convention

import com.sorrowblue.comicviewer.libs

plugins {
    id("comicviewer.convention.multiplatform-library")
    id("comicviewer.convention.multiplatform-compose")
    com.github.skydoves.navgraph
    com.google.devtools.ksp
}

tasks.configureEach {
    if (name == "renderNavGraphGalleryLayoutlib" || name == "renderNavGraphLayoutlib") {
        // navgraphのlayoutlib描画がandroidAppのマージ済みassetsを参照するため、実行順依存を明示する
        dependsOn(":app:android:mergeDebugAssets")
    }
}

kotlin {
    android {
        androidResources.enable = true
        withHostTest {
            isIncludeAndroidResources = true
        }
    }
    sourceSets {
        commonMain.dependencies {
            implementation(project(":framework:designsystem"))
            implementation(project(":framework:ui"))
            implementation(project(":domain:usecase"))

            // Image
            implementation(libs.coil3.compose)
            // Paging
            implementation(libs.androidx.pagingCommon)

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
            implementation(libs.androidx.navigationeventCompose)
            implementation(libs.kotlinx.serializationCore)
            implementation(libs.navigation3.resultstate)

            // Paging
            implementation(libs.androidx.pagingCompose)
            implementation(libs.metro.viewmodelCompose)
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }

        }
    }
}
