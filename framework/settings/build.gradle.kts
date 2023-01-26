plugins {
    id("build-logic.android.library")
}

android {
    resourcePrefix("framework_settings")
    viewBinding.enable = true
}

dependencies {
    api(projects.framework.ui)
    api(libs.androidx.preference.ktx)
}
