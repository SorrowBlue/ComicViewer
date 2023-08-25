plugins {
    id("build-logic.android.library")
    id("com.sorrowblue.dagger-hilt")
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
}

android {
    resourcePrefix("favorite")

    buildFeatures {
        dataBinding = true
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
    }
}

dependencies {
    implementation(projects.framework.ui)
    implementation(projects.framework.compose)
    implementation(projects.domain)
    implementation(projects.book)
    implementation(projects.file)
    implementation(projects.folder)
}
