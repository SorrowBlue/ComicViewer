@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("build-logic.android.library")
}

dependencies {
    implementation(projects.framework)
    api(projects.domain.common)
    api(libs.androidx.paging.common)
}
