plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.settings.common)
                implementation(libs.kotlinx.datetime)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.browser)
                implementation(libs.mikepenz.aboutlibrariesComposeM3)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.settings.info"
    resourcePrefix("settings_info")
}
