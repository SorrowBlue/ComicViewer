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
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.settings.folder"
    resourcePrefix("settings_folder")
}
