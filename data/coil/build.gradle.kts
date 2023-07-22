plugins {
    id("build-logic.android.library")
    id("com.sorrowblue.dagger-hilt")
    alias(libs.plugins.kotlin.plugin.serialization)
}

dependencies {
    implementation(projects.framework)
    implementation(projects.domain.common)
    implementation(projects.data)
    implementation(projects.data.reader)

    implementation(libs.androidx.startup.runtime)
    implementation(libs.androidx.exifinterface)
    implementation(libs.coil)
    implementation(libs.kotlinx.serialization.protobuf)
}
