@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("build-logic.android.library")
    id("com.sorrowblue.dagger-hilt")
}

dependencies {
    implementation(projects.framework)
    implementation(projects.framework.notification)
    implementation(projects.data)

    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.work.runtime.ktx)
}
