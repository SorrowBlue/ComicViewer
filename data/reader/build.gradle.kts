@Suppress("DSL_SCOPE_VIOLATION")
plugins {

    id("build-logic.android.library")
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.dagger.hilt.android)
}

dependencies {
    implementation(libs.dagger.hilt.android.core)
    kapt(libs.dagger.hilt.android.compiler)
}

kapt {
    correctErrorTypes = true
}
