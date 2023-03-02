plugins {
    id("build-logic.android.dynamic-feature")
}

dependencies {
    implementation(projects.app)
    implementation(projects.data.common)
    implementation(projects.data.reader)

    implementation(libs.squareup.logcat)
    implementation(libs.androidx.startup.runtime)
    implementation(libs.github.omicronapps.sevenZipJBinding4Android)
}
