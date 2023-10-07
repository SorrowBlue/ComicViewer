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
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.coil.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigationCompose)
    implementation(libs.androidx.lifecycle.viewmodelKtx)
    api(libs.kotlinx.collections.immutable)
    implementation(libs.androidx.core.ktx)
    api(libs.androidx.paging.compose)
}
