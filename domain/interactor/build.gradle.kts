plugins {
    id("build-logic.android.library")
    id("com.sorrowblue.dagger-hilt")
}

android {
    namespace = "com.sorrowblue.comicviewer.domain.interactor"
}

dependencies {
    implementation(projects.framework)
    implementation(projects.domain)

    implementation(libs.androidx.paging.common)
}
