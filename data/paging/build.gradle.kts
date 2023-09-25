plugins {
    id("comicviewer.android.library")
    id("comicviewer.android.hilt")
}

android {
    namespace = "com.sorrowblue.comicviewer.data.paging"
}

dependencies {
    implementation(projects.framework)
    implementation(projects.domain.model)
    implementation(projects.data)
    implementation(projects.data.database)

    implementation(libs.androidx.paging.common)
}
