plugins {
    id("build-logic.android.library")
}

android {
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.8-dev-k1.8.22-badc51991ec"
    }
}

dependencies {
    api(projects.framework)
    api(projects.framework.resource)

    api(libs.androidx.fragment.ktx)
    api(libs.bundles.androidx.lifecycle)
    api(libs.bundles.androidx.navigation)

    api(platform(libs.androidx.compose.bom))
    api(libs.bundles.androidx.compose)
    api(libs.androidx.hilt.navigation.compose)
    api(libs.coil.compose)
    api(libs.androidx.paging.compose)
    api(libs.google.accompanist.themeadapter.material3)
    debugImplementation(libs.bundles.androidx.compose.debug)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
