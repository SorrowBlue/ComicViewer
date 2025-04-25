plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.domain.service)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.startup.runtime)
                implementation(libs.google.android.play.feature.delivery.ktx)
                implementation(libs.koin.androidxStartup)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.data.storage"
}
