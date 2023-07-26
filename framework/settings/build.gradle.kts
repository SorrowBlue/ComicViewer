plugins {
    id("build-logic.android.library")
}

android {
    resourcePrefix("framework_settings")

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    api(projects.framework.ui)
    api(libs.androidx.preference.ktx)
}
