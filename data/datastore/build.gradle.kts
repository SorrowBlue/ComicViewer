plugins {
    id("comicviewer.android.library")
    id("comicviewer.android.hilt")
}

android {
    namespace = "com.sorrowblue.comicviewer.data.datastore"
}

dependencies {
    implementation(projects.data.infrastructure)

    implementation(libs.androidx.datastore)
    implementation(libs.kotlinx.serialization.protobuf)
}
