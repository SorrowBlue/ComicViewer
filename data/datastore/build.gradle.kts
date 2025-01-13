plugins {
    alias(libs.plugins.comicviewer.android.library)
    alias(libs.plugins.comicviewer.android.hilt)
}

android {
    namespace = "com.sorrowblue.comicviewer.data.datastore"
}

dependencies {
    implementation(projects.domain.service)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.startup.runtime)
    implementation(libs.google.android.play.feature.delivery.ktx)
    implementation(libs.kotlinx.serialization.protobuf)
}
