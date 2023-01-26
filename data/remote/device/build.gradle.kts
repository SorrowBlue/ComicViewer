@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("build-logic.android.library")
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.dagger.hilt.android)
}

dependencies {
    implementation(projects.framework)
    implementation(projects.data.remote)

    implementation(libs.squareup.logcat)
    implementation(libs.androidx.documentfile)

    implementation(libs.dagger.hilt.android.core)
    kapt(libs.dagger.hilt.android.compiler)

    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.bundles.androidx.instrumented.tests)
}

kapt {
    correctErrorTypes = true
}
