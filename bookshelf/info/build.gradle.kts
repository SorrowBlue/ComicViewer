@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("build-logic.android.library")
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
}

android {
    resourcePrefix("bookshelf_info")
    dataBinding.enable = true
    viewBinding.enable = true
}

dependencies {
    implementation(projects.framework)
    implementation(projects.framework.ui)
    implementation(projects.domain)

    implementation(projects.bookshelf.management)

    implementation(libs.dagger.hilt.android.core)
    kapt(libs.dagger.hilt.android.compiler)
}

kapt {
    correctErrorTypes = true
}