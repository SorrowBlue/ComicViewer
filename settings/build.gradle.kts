plugins {
    id("build-logic.android.library")
    id("com.sorrowblue.dagger-hilt")
    id("org.jetbrains.kotlin.kapt")
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
}

android {
    resourcePrefix("settings")
    defaultConfig {
        buildConfigField("long", "TIMESTAMP", "${System.currentTimeMillis()}L")
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
    }
}

dependencies {
    implementation(projects.framework.compose)
    implementation(projects.framework.settings)
    implementation(projects.domain)
    implementation(projects.settings.display)
    implementation(projects.settings.viewer)
    implementation(projects.settings.folder)
    implementation(projects.settings.security)

    implementation(libs.mikepenz.aboutlibraries)
    implementation(libs.androidx.browser)
    implementation("com.google.android.play:review-ktx:2.0.1")
    implementation(libs.androidx.hilt.navigation.fragment)
}
