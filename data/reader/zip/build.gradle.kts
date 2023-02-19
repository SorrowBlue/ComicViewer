plugins {
    id("com.android.library")
id("org.jetbrains.kotlin.android")
id("build-logic.android.library")

    id("org.jetbrains.kotlin.kapt")
    id("dagger.hilt.android.plugin")
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
