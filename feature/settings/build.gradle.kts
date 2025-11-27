plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.feature.settings"
        // resourcePrefix("settings")
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.settings.common)
                implementation(projects.feature.settings.display)
                implementation(projects.feature.settings.folder)
                implementation(projects.feature.settings.info)
                implementation(projects.feature.settings.security)
                implementation(projects.feature.settings.plugin)
                implementation(projects.feature.settings.viewer)
                implementation(projects.feature.authentication)
                implementation(projects.feature.tutorial)
                implementation(libs.androidx.coreUri)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.appcompat)
            }
        }
    }
}
