plugins {
    id("build-logic.android.library")
    id("com.sorrowblue.dagger-hilt")
}

android {
    namespace = "com.sorrowblue.comicviewer.data.datastore"
}

dependencies {
    implementation(projects.framework)
    implementation(projects.domain.common)
    implementation(projects.data)

    implementation(libs.androidx.datastore)
    implementation(libs.kotlinx.serialization.protobuf)
}
