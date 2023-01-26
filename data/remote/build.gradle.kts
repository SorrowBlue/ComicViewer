@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("build-logic.android.library")
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.kotlin.plugin.serialization)
}

dependencies {
    implementation(projects.framework)
    implementation(projects.data)
    api(projects.data.reader)
    api(projects.data.common)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.coil)
    implementation(libs.androidx.startup.runtime)
    implementation(libs.squareup.logcat)

    implementation(libs.dagger.hilt.android.core)
    implementation(libs.androidx.exifinterface)
    kapt(libs.dagger.hilt.android.compiler)

    androidTestImplementation(libs.androidx.test.ext.junit.ktx)
    androidTestImplementation(libs.androidx.test.espresso.core)
}

kapt {
    correctErrorTypes = true
}
