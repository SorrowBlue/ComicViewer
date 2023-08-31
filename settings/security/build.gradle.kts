plugins {
    id("build-logic.android.library")
    id("com.sorrowblue.dagger-hilt")
    id("org.jetbrains.kotlin.kapt")
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
}

android {
    resourcePrefix("settings_security")

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

kapt {
    correctErrorTypes = true
}
dependencies {
    implementation(projects.framework.settings)
    implementation(projects.domain)

    implementation(libs.androidx.biometric)

    implementation(libs.androidx.hilt.navigation.fragment)
}
