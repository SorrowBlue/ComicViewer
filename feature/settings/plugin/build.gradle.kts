plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.settings.common)
            }
        }
        desktopMain {
            dependencies {
                implementation(libs.filekit.compose)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.settings.plugin"
    resourcePrefix("settings_plugin")
}
