@file:Suppress("UnstableApiUsage")

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("build-logic.android.dynamic-feature")
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
}

android {
    resourcePrefix("onedrive")

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    implementation(projects.app)
    implementation(projects.dynamic)
    implementation("com.fasterxml.jackson.core:jackson-core:2.13.4")

    implementation(libs.kotlinx.coroutines.jdk8)
    implementation(libs.microsoft.graph)
    implementation(libs.microsoft.identity.client.msal)

    implementation(libs.androidx.work.runtime.ktx)
}

kapt {
    correctErrorTypes = true
}
