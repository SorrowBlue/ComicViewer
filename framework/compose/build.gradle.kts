plugins {
    id("comicviewer.android.library")
    id("comicviewer.android.library.compose")
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.compose"
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
    }
}

dependencies {
    api(projects.framework)
    api(projects.framework.resource)

    api(libs.bundles.androidx.compose)

    api(libs.androidx.appcompat)
    api(libs.androidx.core.ktx)
    api(libs.androidx.hilt.navigationCompose)
    api(libs.androidx.navigation.compose)
    api(libs.androidx.paging.compose)
    api(libs.androidx.lifecycle.compose)
    api(libs.androidx.window)
    api(libs.coil.compose)
    api(libs.google.accompanist.navigation.material)
    api(libs.kotlinx.collections.immutable)

    debugImplementation(libs.bundles.androidx.compose.debug)

    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
