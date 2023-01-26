@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("build-logic.android.library")
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.dagger.hilt.android)
}

dependencies {
    implementation(projects.framework)
    implementation(projects.domain.interactor)
    implementation(projects.data)
    implementation(projects.data.common)
    implementation(projects.data.database)

    implementation(libs.squareup.logcat)
    implementation(libs.dagger.hilt.android.core)
    kapt(libs.dagger.hilt.android.compiler)
}

kapt {
    correctErrorTypes = true
}
