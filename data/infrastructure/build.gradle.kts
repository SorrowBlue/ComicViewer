plugins {
    id("comicviewer.android.library")
    id("comicviewer.android.hilt")
}

android {
    namespace = "com.sorrowblue.comicviewer.data.infrastructure"
}

dependencies {
    api(projects.domain.model)
    implementation(projects.domain.service)
    implementation(projects.data.reader)

    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.squareup.logcat)
    implementation(libs.androidx.work.runtime.ktx)
}
