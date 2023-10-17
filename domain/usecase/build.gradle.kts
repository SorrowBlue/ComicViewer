plugins {
    id("comicviewer.android.library")
    id("comicviewer.android.hilt")
}

android {
    namespace = "com.sorrowblue.comicviewer.domain.usecase"
}

dependencies {
    api(projects.domain.model)

    implementation(libs.androidx.paging.common)
}
