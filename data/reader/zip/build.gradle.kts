import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
}

kotlin {
    sourceSets.commonMain.dependencies {
        implementation(projects.data.storage.client)
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlinx.io)
    }

    sourceSets.androidMain.dependencies {
        implementation(libs.androidx.startup.runtime)
        implementation(libs.com.sorrowblue.sevenzipjbinding)
    }

    sourceSets.desktopMain.dependencies {
        implementation(libs.sevenzipjbinding)
        implementation(libs.sevenzipjbinding.allPlatforms)
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
