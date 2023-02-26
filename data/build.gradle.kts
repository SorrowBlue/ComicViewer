@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("build-logic.android.library")
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.kotlin.plugin.serialization)
}

dependencies {
    implementation(projects.framework)
    implementation(projects.domain.interactor)
    implementation(projects.data.common)
    implementation(projects.data.reader)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.squareup.logcat)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.coil)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.exifinterface)

    implementation(libs.dagger.hilt.android.core)
    kapt(libs.dagger.hilt.android.compiler)
}

kapt {
    correctErrorTypes = true
}
