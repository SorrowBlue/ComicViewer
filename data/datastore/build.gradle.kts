@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("build-logic.android.library")
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.dagger.hilt.android)
}

dependencies {
    implementation(projects.framework)
    implementation(projects.data)
    implementation(projects.domain)

    implementation(libs.dagger.hilt.android.core)
    kapt(libs.dagger.hilt.android.compiler)

    implementation(libs.androidx.datastore)
    implementation(libs.kotlinx.serialization.protobuf)
}

kapt {
    correctErrorTypes = true
}
