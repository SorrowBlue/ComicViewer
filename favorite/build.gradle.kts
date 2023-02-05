@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("build-logic.android.library")
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
    alias(libs.plugins.dagger.hilt.android)
}

android {
    resourcePrefix("favorite_")
    dataBinding.enable = true
    viewBinding.enable = true
}

dependencies {
    implementation(projects.framework)
    implementation(projects.framework.ui)
    implementation(projects.domain)
    implementation(projects.book)
    implementation(projects.folder)
    implementation(projects.folder.display)

    implementation(libs.dagger.hilt.android.core)
    kapt(libs.dagger.hilt.android.compiler)
}

kapt {
    correctErrorTypes = true
}
