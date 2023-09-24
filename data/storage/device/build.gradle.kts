plugins {
    id("comicviewer.android.library")
    id("comicviewer.android.hilt")
}

android {
    namespace = "com.sorrowblue.comicviewer.data.storage.device"
}

dependencies {
    implementation(projects.framework)
    implementation(projects.data.storage)

    implementation(libs.androidx.documentfile)
}
