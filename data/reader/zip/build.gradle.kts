plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
}

kotlin {
    sourceSets.commonMain.dependencies {
        implementation(projects.data.storage.client)
        implementation(libs.kotlinx.coroutines.core)
    }

    sourceSets.androidMain.dependencies {
        implementation(libs.androidx.startup.runtime)
        implementation(libs.com.sorrowblue.sevenzipjbinding)
    }

    sourceSets.desktopMain.dependencies {
        implementation("net.sf.sevenzipjbinding:sevenzipjbinding:16.02-2.01")
        implementation("net.sf.sevenzipjbinding:sevenzipjbinding-all-platforms:16.02-2.01")
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
