plugins {
    alias(libs.plugins.comicviewer.android.library)
    alias(libs.plugins.comicviewer.android.hilt)
}

android {
    namespace = "com.sorrowblue.comicviewer.data.reader.zip"
    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
    }
}

dependencies {
    implementation(projects.data.storage.client)

    implementation(libs.androidx.startup.runtime)
    implementation(libs.com.sorrowblue.sevenzipjbinding)
}
