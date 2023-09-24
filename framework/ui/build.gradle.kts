plugins {
    id("comicviewer.android.library")
    id("comicviewer.android.library.compose")
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.ui"
}

dependencies {
    implementation(projects.domain.common)
    implementation(projects.framework.designsystem)

    api(libs.androidx.compose.material3)
    api(libs.androidx.hilt.navigation.compose)
    api(libs.androidx.paging.compose)
    api(libs.androidx.compose.material.icons.extended)
    api(libs.coil.compose)
}
