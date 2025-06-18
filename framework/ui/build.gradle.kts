plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.compose)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.framework.designsystem)
                implementation(projects.domain.model)

                // Material3
                implementation(compose.material3)
                implementation(compose.material3AdaptiveNavigationSuite) {
                    exclude(group = "org.jetbrains.androidx.window")
                }
                implementation(libs.compose.multiplatform.material3.adaptiveLayout) {
                        exclude(group = "org.jetbrains.androidx.window")
                }
                implementation(libs.compose.multiplatform.material3.adaptiveNavigation) {
                    exclude(group = "org.jetbrains.androidx.window")
                }
                // Navigation + Serialization
                implementation(libs.kotlinx.serialization.cbor)
                // Image
                implementation(libs.coil3.compose)
                // Paging
                implementation(libs.androidx.paging.common)
                // Di
                implementation(libs.koin.composeViewModel)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.activity.compose)
                implementation(libs.drick.compose.edgeToEdgePreview)
            }
        }
    }
}

compose {
    resources {
        publicResClass = true
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.ui"
}
