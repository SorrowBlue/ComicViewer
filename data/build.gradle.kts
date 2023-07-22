@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("build-logic.android.library")
    id("com.sorrowblue.dagger-hilt")
    alias(libs.plugins.kotlin.plugin.serialization)
}

dependencies {
    implementation(projects.framework)
    implementation(projects.domain.common)
    implementation(projects.domain.interactor)
    api(projects.data.common)
    implementation(projects.data.reader)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.squareup.logcat)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.kotlinx.serialization.protobuf)
}
