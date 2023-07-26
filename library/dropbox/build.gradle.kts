plugins {
    id("build-logic.android.dynamic-feature")
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
}

android {
    resourcePrefix("dropbox")

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    implementation(projects.app)
    implementation(projects.framework.ui)
    implementation(projects.framework.notification)
    implementation(projects.domain)
    implementation(projects.library)

    implementation(libs.androidx.datastore)
    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.dropbox.core.sdk)
    implementation(libs.kotlinx.serialization.protobuf)
}

kapt {
    correctErrorTypes = true
}
