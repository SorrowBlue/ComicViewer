plugins {
    id("build-logic.android.dynamic-feature")
}

dependencies {
    implementation(projects.app)

    api(libs.androidx.browser)
    api("com.fasterxml.jackson.core:jackson-core:2.14.2")
    api(libs.google.code.gson)
}
