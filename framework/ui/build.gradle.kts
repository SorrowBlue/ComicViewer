plugins {
    alias(libs.plugins.comicviewer.android.library)
    alias(libs.plugins.comicviewer.android.compose)
    alias(libs.plugins.comicviewer.koin)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.ui"
}
composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    metricsDestination = layout.buildDirectory.dir("compose_compiler")
}


dependencies {
    implementation(projects.domain.model)
    implementation(projects.framework.designsystem)

    api(libs.androidx.window)
    api(libs.androidx.compose.material3.adaptive.layout)
    api(libs.androidx.compose.material3.adaptive.navigation)
    api(libs.androidx.compose.material3.adaptiveNavigationSuite)
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.drick.compose.edgeToEdgePreview)

    api(libs.androidx.hilt.navigationCompose)
    api(libs.androidx.lifecycle.viewmodel)
    api(libs.androidx.paging.compose)

    api(libs.coil3.compose)
    api(libs.coil3.test)
    api(libs.coil3.networkKtor)

    api(libs.androidx.compose.ui.toolingPreview)
    debugApi(libs.androidx.compose.ui.tooling)
}
