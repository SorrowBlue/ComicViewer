plugins {
    id("comicviewer.android.library")
    id("comicviewer.android.hilt")
}

android {
    namespace = "com.sorrowblue.comicviewer.data.smb"
}

dependencies {
    implementation(projects.data.storage.client)

    implementation(libs.jcifs.ng)
}
