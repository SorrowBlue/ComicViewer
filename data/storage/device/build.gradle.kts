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
            implementation(libs.androidx.documentfile)
        }
        iosMain.dependencies {
            implementation("dev.zwander:kmpfile-filekit:0.7.0")
            implementation("dev.zwander:kmpfile-okio:0.7.0")
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.data.storage.device"
}
