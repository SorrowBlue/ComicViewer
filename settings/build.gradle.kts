@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

plugins {
    id("build-logic.android.library")
    id("com.sorrowblue.dagger-hilt")
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
}

android {
    resourcePrefix("settings")

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
    implementation(projects.settings.folder)
    implementation(projects.settings.security)

    implementation(libs.mikepenz.aboutlibraries)

    implementation(libs.androidx.hilt.navigation.fragment)
    kapt(libs.androidx.hilt.compiler)
}
