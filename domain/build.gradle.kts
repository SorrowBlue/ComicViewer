plugins {
    id("build-logic.android.library")
}

dependencies {
    implementation(projects.framework)
    api(projects.domain.common)

    implementation(libs.androidx.paging.common)
}
