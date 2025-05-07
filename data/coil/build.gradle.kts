plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.data.coil"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.domain.service)
                implementation(libs.coil3)
                implementation(libs.kotlinx.serialization.json)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.startup.runtime)
                implementation(libs.coil3.networkKtor)
                implementation(libs.koin.androidxStartup)
            }
        }
    }
}
