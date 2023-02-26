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

    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.microsoft.graph)
    implementation(libs.microsoft.identity.client.msal)

    implementation(libs.kotlinx.coroutines.jdk8)
}

kapt {
    correctErrorTypes = true
}
