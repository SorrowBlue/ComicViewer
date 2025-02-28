plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.data.storage.client)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.filekit.compose)
            implementation(libs.squareup.okio)
            implementation(libs.compose.multiplatform.coreUri)
        }
        androidMain.dependencies {
            implementation(libs.jcifs.ng)
            implementation(libs.slf4j.android)
            implementation(libs.androidx.documentfile)
        }
        desktopMain.dependencies {
            implementation(libs.jcifs.ng)
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.data.smb"
}
