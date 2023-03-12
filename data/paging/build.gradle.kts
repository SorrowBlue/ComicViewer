@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("build-logic.android.library")
    id("com.sorrowblue.dagger-hilt")
}

dependencies {
    implementation(projects.framework)
    implementation(projects.domain.interactor)
    implementation(projects.data)
    implementation(projects.data.common)
    implementation(projects.data.database)

    implementation(libs.squareup.logcat)
}
