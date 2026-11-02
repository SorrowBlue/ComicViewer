plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.feature.settings.display"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.settings.common)
                implementation(projects.feature.settings.nav)
                implementation(libs.kotlinx.collectionsImmutable)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.appcompat)
            }
        }
    }
}
