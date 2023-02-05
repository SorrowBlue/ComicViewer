@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("build-logic.android.library")
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
}

android {
    resourcePrefix("settings_")
    dataBinding.enable = true
    viewBinding.enable = true
}

dependencies {
    implementation(projects.framework)
    implementation(projects.framework.settings)
    implementation(projects.domain)
    implementation(projects.settings.display)
    implementation(projects.settings.viewer)
    implementation(projects.settings.folder)
    implementation(projects.settings.security)
    implementation(libs.androidx.appcompat)

    implementation(libs.androidx.preference.ktx)

    implementation(libs.dagger.hilt.android.core)
    kapt(libs.dagger.hilt.android.compiler)

    implementation(libs.androidx.hilt.navigation.fragment)
    kapt(libs.androidx.hilt.compiler)
}

kapt {
    correctErrorTypes = true
}
