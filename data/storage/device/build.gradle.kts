plugins {
    id("build-logic.android.library")
    id("com.sorrowblue.dagger-hilt")
}

android {
    namespace = "com.sorrowblue.comicviewer.data.storage.device"
}

dependencies {
    implementation(projects.framework)
    implementation(projects.data.storage)

    implementation(libs.androidx.documentfile)
}
