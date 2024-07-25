plugins {
    alias(libs.plugins.comicviewer.android.library)
    alias(libs.plugins.comicviewer.android.compose)
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.preview"
}

dependencies {
    implementation(projects.framework.designsystem)
    implementation(projects.domain.model)

    implementation(libs.androidx.paging.common)
    implementation(libs.androidx.compose.ui.toolingPreview)
    implementation(libs.androidx.compose.material3.adaptive.layout)
}
