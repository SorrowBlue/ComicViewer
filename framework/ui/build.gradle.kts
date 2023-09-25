plugins {
    id("comicviewer.android.library")
    id("comicviewer.android.library.compose")
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.ui"
}

dependencies {
    implementation(projects.domain.model)
    implementation(projects.framework.designsystem)

    api(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.viewmodelKtx)
    implementation(libs.androidx.core.ktx)
    api(libs.androidx.paging.compose)
}
