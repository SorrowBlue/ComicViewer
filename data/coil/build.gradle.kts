plugins {
    id("comicviewer.android.library")
    id("comicviewer.android.hilt")
    alias(libs.plugins.kotlin.plugin.serialization)
}

android {
    namespace = "com.sorrowblue.comicviewer.data.coil"
}

dependencies {
    implementation(projects.framework.common)
    implementation(projects.domain.service)
    implementation(projects.data.reader)

    implementation(libs.androidx.startup.runtime)
    implementation(libs.androidx.exifinterface)
    implementation(libs.coil)
    implementation(libs.jcifs.ng)
    implementation(libs.kotlinx.serialization.protobuf)
}
