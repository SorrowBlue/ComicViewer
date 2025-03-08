plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.settings.common)
                implementation(projects.feature.settings.display)
                implementation(projects.feature.settings.folder)
                implementation(projects.feature.settings.info)
                implementation(projects.feature.settings.security)
                implementation(projects.feature.settings.viewer)
                implementation(projects.feature.authentication)
                implementation(projects.feature.tutorial)
                implementation(compose.material3AdaptiveNavigationSuite)
                implementation(libs.compose.multiplatform.coreUri)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.appcompat)
                implementation(libs.google.android.billingclient.billingKtx)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.settings"
    resourcePrefix("settings")
}
