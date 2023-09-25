plugins {
    id("comicviewer.android.library")
    id("comicviewer.android.hilt")
}

android {
    namespace = "com.sorrowblue.comicviewer.domain.service"
}

dependencies {
    implementation(projects.framework)
    implementation(projects.domain.usecase)

    implementation(libs.androidx.paging.common)
}
