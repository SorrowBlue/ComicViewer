plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.comicviewer.di)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.framework.notification"
        androidResources.enable = true
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.compose.componentsResources)
                implementation(libs.compose.runtime)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.coreKtx)
                implementation(libs.androidx.startupRuntime)
            }
        }
    }
}
