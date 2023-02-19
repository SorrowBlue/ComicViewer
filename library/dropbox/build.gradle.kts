@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("build-logic.android.library")
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
    alias(libs.plugins.dagger.hilt.android)
}

android {
    resourcePrefix("dropbox")

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    implementation(projects.framework.ui)
    implementation(projects.framework.notification)
    implementation(projects.domain)

    implementation(libs.androidx.datastore)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.dropbox.core.sdk)
    implementation(libs.kotlinx.serialization.protobuf)

    implementation(libs.androidx.hilt.work)
    kapt(libs.androidx.hilt.compiler)

    implementation(libs.dagger.hilt.android.core)
    kapt(libs.dagger.hilt.android.compiler)
}

kapt {
    correctErrorTypes = true
}
