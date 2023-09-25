plugins {
    id("comicviewer.android.library")
    id("comicviewer.android.hilt")
}

android {
    namespace = "com.sorrowblue.comicviewer.data.paging"
}

dependencies {
    implementation(projects.data.infrastructure)
    implementation(projects.data.database)

    implementation(libs.androidx.paging.common)
}
