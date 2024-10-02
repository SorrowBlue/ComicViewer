plugins {
    alias(libs.plugins.comicviewer.android.library)
    alias(libs.plugins.comicviewer.android.hilt)
}

android {
    namespace = "com.sorrowblue.comicviewer.data.smb"
}

dependencies {
    implementation(projects.data.storage.client)

    implementation(libs.jcifs.ng)
    implementation(libs.slf4j.android)
}
