plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.comicviewer.multiplatformCompose)
    alias(libs.plugins.comicviewer.di)
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

                // Navigation + Serialization
                implementation(libs.kotlinx.serializationCbor)
                // Image
                implementation(libs.coil3.compose)
                // Paging
                implementation(libs.androidx.pagingCommon)
                implementation(libs.androidx.pagingCompose)
                implementation(libs.composables.core)
                implementation(libs.androidx.collection)
            }
        }
        androidMain {
            dependencies {
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
