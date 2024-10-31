plugins {
    alias(libs.plugins.comicviewer.android.library)
    alias(libs.plugins.comicviewer.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.sorrowblue.comicviewer.data.coil"
}

dependencies {
    implementation(projects.framework.common)
    implementation(projects.domain.service)
    implementation(projects.domain.reader)

    implementation(libs.androidx.startup.runtime)
    implementation(libs.androidx.exifinterface)
    implementation(libs.coil3)
    implementation(libs.coil3.networkKtor)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.jcifs.ng)
    implementation(libs.kotlinx.serialization.protobuf)
}
