plugins {
    alias(libs.plugins.comicviewer.android.library)
    alias(libs.plugins.comicviewer.android.compose)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.ui"
}

dependencies {
//    implementation(projects.domain.model)
    implementation(projects.framework.designsystem)

    api(libs.androidx.window)
    api(libs.androidx.compose.material3.adaptive.layout)
    api(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.ui.util)

    api(libs.kotlinx.collections.immutable)

    api(libs.androidx.hilt.navigationCompose)
    api(libs.androidx.lifecycle.viewmodelKtx)
    api(libs.androidx.paging.compose)

    api(libs.coil3.compose)
    api(libs.coil3.networkKtor)

    api(libs.androidx.compose.ui.toolingPreview)
    debugApi(libs.androidx.compose.ui.tooling)
}
