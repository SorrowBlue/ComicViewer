plugins {
    id("build-logic.android.library")
}

android {
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
    implementation(projects.framework.ui)

    api(libs.androidx.fragment.ktx)
    api(libs.bundles.androidx.lifecycle)
    api(libs.bundles.androidx.navigation)

    api(platform(libs.androidx.compose.bom))
    api(libs.bundles.androidx.compose)
    api(libs.androidx.window)
    implementation("androidx.compose.ui:ui-util:1.5.0")
    api("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.5")
    api("com.google.accompanist:accompanist-permissions:0.33.0-alpha")
    api("com.google.accompanist:accompanist-systemuicontroller:0.33.0-alpha")
    api(libs.androidx.hilt.navigation.compose)
    api(libs.coil.compose)
    api(libs.androidx.paging.compose)
    debugImplementation(libs.bundles.androidx.compose.debug)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
