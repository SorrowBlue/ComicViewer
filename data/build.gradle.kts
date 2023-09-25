@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("comicviewer.android.library")
    id("comicviewer.android.hilt")
    alias(libs.plugins.kotlin.plugin.serialization)
}

android {
    namespace = "com.sorrowblue.comicviewer.data"
}

dependencies {
    implementation(projects.framework)
    implementation(projects.domain.model)
    implementation(projects.domain.service)
    api(projects.data.common)
    implementation(projects.data.reader)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.squareup.logcat)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.kotlinx.serialization.protobuf)
}
