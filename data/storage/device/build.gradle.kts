plugins {
    alias(libs.plugins.comicviewer.android.library)
    alias(libs.plugins.comicviewer.android.hilt)
}

android {
    namespace = "com.sorrowblue.comicviewer.data.storage.device"
}

dependencies {
    implementation(projects.data.storage.client)

    implementation(libs.androidx.documentfile)
}
