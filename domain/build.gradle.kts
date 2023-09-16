plugins {
    id("build-logic.android.library")
}

android {
    namespace = "com.sorrowblue.comicviewer.domain"
}

dependencies {
    implementation(projects.framework)
    api(projects.domain.common)

    implementation(libs.androidx.paging.common)
}
