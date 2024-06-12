plugins {
    alias(libs.plugins.comicviewer.android.library)
    alias(libs.plugins.comicviewer.android.hilt)
}

android {
    namespace = "com.sorrowblue.comicviewer.domain.usecase"
}

dependencies {
    api(projects.domain.model)

    implementation(libs.androidx.paging.common)
}
