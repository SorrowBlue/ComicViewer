@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("build-logic.android.library")
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.dagger.hilt.android)
}

dependencies {
    implementation(projects.data.common)
    implementation(projects.data.reader)

    implementation(libs.squareup.logcat)
    implementation(libs.androidx.startup.runtime)
    implementation(libs.github.omicronapps.sevenZipJBinding4Android)

    implementation(libs.dagger.hilt.android.core)
    kapt(libs.dagger.hilt.android.compiler)
}

kapt {
    correctErrorTypes = true
}
