plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.comicviewer.multiplatformCompose)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.framework.ui"
        androidResources.enable = true
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.framework.designsystem)
                implementation(projects.domain.model)

                implementation(libs.androidx.navigation3UI)
                implementation(libs.androidx.lifecycleViewmodelCompose)
                implementation(libs.androidx.pagingCompose)
                implementation(libs.coil3.compose)
                implementation(libs.composables.composeunstyledPrimitive)
                implementation(libs.compose.material3)
                implementation(libs.compose.material3Adaptive)
                implementation(libs.compose.material3AdaptiveLayout)
                implementation(libs.compose.material3AdaptiveNavigation3)
                implementation(libs.compose.material3AdaptiveNavigationSuite)
                implementation(libs.kotlinx.serializationCbor)
                implementation(libs.navigation3.resultstate)
                implementation(libs.rin)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.coreKtx)
                implementation(libs.compose.edgeToEdgePreview)
            }
        }
    }
}

compose {
    resources {
        publicResClass = true
    }
}
