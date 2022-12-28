plugins {
    id("com.android.library")
id("org.jetbrains.kotlin.android")
id("build-logic.android.library")

    id("org.jetbrains.kotlin.kapt")
    id("dagger.hilt.android.plugin")
}

dependencies {
    api(projects.data)
    implementation(projects.data.common)
    implementation(projects.framework.notification)

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.work.multiprocess)
    implementation(libs.squareup.logcat)

    implementation(libs.androidx.hilt.work)
    kapt(libs.androidx.hilt.compiler)

    implementation(libs.dagger.hilt.android.core)
    kapt(libs.dagger.hilt.android.compiler)
}
