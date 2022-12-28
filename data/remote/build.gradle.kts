plugins {
    id("com.android.library")
id("org.jetbrains.kotlin.android")
id("build-logic.android.library")

    id("org.jetbrains.kotlin.kapt")
    kotlin("plugin.serialization")
    id("dagger.hilt.android.plugin")
}

dependencies {
    api(projects.data)
    implementation(projects.data.common)
    implementation(projects.data.remote.client)
    implementation(projects.data.remote.reader)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.coil)
    implementation(libs.androidx.startup.runtime)
    implementation(libs.squareup.logcat)

    implementation(libs.dagger.hilt.android.core)
    implementation(libs.androidx.exifinterface)
    kapt(libs.dagger.hilt.android.compiler)

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}

kapt {
    correctErrorTypes = true
}
