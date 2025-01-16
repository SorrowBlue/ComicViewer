plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
}

kotlin {
    sourceSets.commonMain.dependencies {
        implementation(projects.data.storage.client)
    }

    sourceSets.androidMain.dependencies {
        implementation(libs.jcifs.ng)
        implementation(libs.slf4j.android)
        implementation(libs.androidx.documentfile)
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.data.smb"
}
