plugins {
    id("com.sorrowblue.android-dynamic-feature")
    alias(libs.plugins.kotlin.plugin.serialization)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.library.dropbox"
    resourcePrefix("dropbox")
}

dependencies {
    implementation(projects.app)
    implementation(projects.framework.compose)
    implementation(projects.framework.notification)
    implementation(projects.domain)
    implementation(projects.feature.library)

    implementation(libs.androidx.datastore)
    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.dropbox.core.sdk)
    implementation(libs.kotlinx.serialization.protobuf)
}
