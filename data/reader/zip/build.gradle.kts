plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.di)
}

kotlin {
    sourceSets.commonMain.dependencies {
        implementation(projects.data.storage.client)
    }

    sourceSets.androidMain.dependencies {
        implementation(libs.androidx.startup.runtime)
        implementation(libs.com.sorrowblue.sevenzipjbinding)
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.data.reader.zip"
    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
    }
}
