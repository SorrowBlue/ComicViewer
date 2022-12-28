plugins {
    id("com.android.library")
id("org.jetbrains.kotlin.android")
id("build-logic.android.library")

}

android {
    resourcePrefix = "framework_settings"
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    api(projects.framework.ui)
    api(libs.androidx.preference.ktx)
}
