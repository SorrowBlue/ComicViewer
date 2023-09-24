plugins {
    id("comicviewer.android.library")
    id("comicviewer.android.hilt")
}

android {
    namespace = "com.sorrowblue.comicviewer.domain.interactor"
}

dependencies {
    implementation(projects.framework)
    implementation(projects.domain)

    implementation(libs.androidx.paging.common)
}
