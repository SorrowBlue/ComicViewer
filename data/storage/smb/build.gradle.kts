plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.data.storage.client)
            implementation(libs.kotlinx.coroutines.core)
            implementation("io.github.vinceglb:filekit-compose:0.8.8")
            implementation("dev.zwander:kmpfile:0.7.0")
            // Convenience functions for converting from FileKit to KMPFile.
            // Supports the same platforms as FileKit, minus JS and WASM.
            implementation("dev.zwander:kmpfile-filekit:0.7.0")
            implementation("dev.zwander:kmpfile-okio:0.7.0")
            implementation("com.squareup.okio:okio:3.10.2")
            implementation("org.jetbrains.androidx.core:core-uri:1.1.0-alpha02")
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
