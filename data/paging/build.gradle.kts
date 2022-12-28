plugins {
    id("com.android.library")
id("org.jetbrains.kotlin.android")
id("build-logic.android.library")

    id("org.jetbrains.kotlin.kapt")
    id("dagger.hilt.android.plugin")
}

dependencies {
    implementation(projects.domain.interactor)
    implementation(projects.data)
    implementation(projects.data.common)
    implementation(projects.data.database)

    implementation(libs.squareup.logcat)
    implementation(libs.dagger.hilt.android.core)
    kapt(libs.dagger.hilt.android.compiler)
}
