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
        implementation(libs.androidx.documentfile)
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.data.storage.device"
}
