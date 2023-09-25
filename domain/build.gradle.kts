plugins {
    id("comicviewer.android.library")
}

android {
    namespace = "com.sorrowblue.comicviewer.domain"
}

dependencies {
    implementation(projects.framework)
    api(projects.domain.model)

    implementation(libs.androidx.paging.common)
}
