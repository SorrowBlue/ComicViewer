plugins {
    id("build-logic.android.library")
    id("com.sorrowblue.dagger-hilt")
}

dependencies {
    implementation(projects.framework)
    implementation(projects.domain.common)
    implementation(projects.data)
    implementation(projects.data.database)

    implementation(libs.androidx.paging.common)
}
