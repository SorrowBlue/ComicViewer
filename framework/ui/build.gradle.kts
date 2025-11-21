plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.compose)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.di)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.framework.designsystem)
                implementation(projects.domain.model)

                // Navigation + Serialization
                implementation(libs.kotlinx.serialization.cbor)
                // Image
                implementation(libs.coil3.compose)
                // Paging
                implementation(libs.androidx.paging.common)
                implementation(libs.androidx.paging.compose)
                implementation(libs.composables.core)
                implementation(libs.androidx.collection)
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
