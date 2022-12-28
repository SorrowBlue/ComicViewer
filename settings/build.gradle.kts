plugins {
    id("com.android.library")
id("org.jetbrains.kotlin.android")
    id("build-logic.android.library")

    id("org.jetbrains.kotlin.kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.sorrowblue.comicviewer.settings"
    resourcePrefix = "settings_"
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    implementation(projects.framework.settings)
    implementation(projects.domain)
    implementation(projects.settings.display)
    implementation(projects.settings.viewer)
    implementation(projects.settings.bookshelf)
    implementation(libs.androidx.appcompat)

//    implementation(libs.androidx.biometric)
    implementation(libs.androidx.preference.ktx)

    implementation(libs.dagger.hilt.android.core)
    kapt(libs.dagger.hilt.android.compiler)
}
