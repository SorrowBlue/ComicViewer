plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.comicviewer.multiplatformCompose)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.framework.ui"
        androidResources.enable = true
        withHostTest {}
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.framework.common)
                implementation(projects.framework.designsystem)
                implementation(projects.framework.navigation)
                implementation(projects.domain.model)

                implementation(libs.kotlinx.serializationCbor)

                implementation(libs.androidx.lifecycleCompose)
                implementation(libs.androidx.lifecycleViewmodelNavigation3)
                implementation(libs.androidx.navigation3UI)
                implementation(libs.androidx.pagingCompose)

                implementation(libs.compose.componentsResources)
                implementation(libs.compose.material3)
                implementation(libs.compose.material3Adaptive)
                implementation(libs.compose.material3AdaptiveLayout)
                implementation(libs.compose.material3AdaptiveNavigation3)
                api(libs.compose.material3AdaptiveNavigationSuite)

                implementation(libs.coil3.compose)
                implementation(libs.composables.composeunstyledScrollbars)
                implementation(libs.metro.viewmodelCompose)
            }
        }
        commonTest {
            dependencies {
                implementation(projects.framework.test)
                implementation(libs.kotlin.test)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.appcompat)
                implementation(libs.androidx.coreKtx)
            }
        }
        jvmMain.dependencies {
            implementation(libs.filekit.core)
        }
    }
}

compose {
    resources {
        publicResClass = true
    }
}
