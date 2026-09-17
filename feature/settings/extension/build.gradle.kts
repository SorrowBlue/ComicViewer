plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.feature.settings.extension"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.settings.common)
                implementation(projects.feature.settings.nav)
            }
        }
        jvmMain {
            dependencies {
                implementation(libs.filekit.compose)
            }
        }
    }
}
