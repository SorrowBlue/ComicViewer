plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.data.storage.client)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.startup.runtime)
                implementation(libs.koin.androidxStartup)
            }
        }
        desktopMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.pdfbox)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.data.reader.document"
    buildFeatures {
        aidl = true
    }
}
