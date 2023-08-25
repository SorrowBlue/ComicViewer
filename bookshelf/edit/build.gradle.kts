plugins {
    id("build-logic.android.library")
    id("com.sorrowblue.dagger-hilt")
}

android {
    resourcePrefix("bookshelf_edit")

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
    }
}

dependencies {
    implementation(projects.framework.compose)
    implementation(projects.domain)
    debugImplementation(libs.bundles.androidx.compose.debug)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
