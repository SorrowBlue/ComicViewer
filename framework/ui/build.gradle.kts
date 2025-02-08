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
                implementation("com.sorrowblue:annotations:1.0")
                implementation(projects.domain.model)
                api(compose.material3)
                api(libs.compose.multiplatform.material3.adaptiveLayout)
                api(libs.compose.multiplatform.material3.adaptiveNavigation)
                api(libs.compose.multiplatform.navigationCompose)
                implementation(compose.material3AdaptiveNavigationSuite)
                implementation(compose.components.resources)
                api(libs.koin.compose)
                api(libs.koin.composeViewModel)
                api(libs.kotlinx.serialization.core)
                api(libs.coil3.compose)
                implementation(libs.kotlinx.serialization.jsonOkio)
                implementation(libs.squareup.okio)
                api(libs.androidx.paging.common)
                implementation(libs.androidx.lifecycle.viewmodel)
            }
        }

        androidMain {
            dependencies {
                api(libs.androidx.compose.ui.toolingPreview)
                implementation(libs.androidx.activity)
                implementation(libs.drick.compose.edgeToEdgePreview)
            }
        }

        desktopMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.swing)
            }
        }
    }
}

compose.resources {
    publicResClass = true
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.ui"
}
