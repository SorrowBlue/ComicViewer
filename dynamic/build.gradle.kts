plugins {
    id("build-logic.android.dynamic-feature")
}

dependencies {
    implementation(projects.app)

    api(libs.androidx.browser)
}
