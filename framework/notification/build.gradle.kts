plugins {
    id("build-logic.android.library")
}

dependencies {
    implementation(projects.framework)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.startup.runtime)
}
