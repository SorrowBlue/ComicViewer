plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.compose)
    alias(libs.plugins.google.ksp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.framework.designsystem)


                implementation(compose.material3)
                implementation("org.jetbrains.compose.material3.adaptive:adaptive:1.1.0-alpha01")
                implementation("org.jetbrains.compose.material3.adaptive:adaptive-layout:1.1.0-alpha01")
                implementation("org.jetbrains.compose.material3.adaptive:adaptive-navigation:1.1.0-alpha01")
                implementation("org.jetbrains.compose.material3:material3-window-size-class:1.7.3")
                implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha11")
                implementation(compose.material3AdaptiveNavigationSuite)

                implementation(compose.components.uiToolingPreview)
                implementation(compose.uiUtil)
            }
        }

        androidMain {
            dependencies {
                implementation(projects.domain.model)
                implementation(projects.framework.designsystem)

                implementation(project.dependencies.platform(libs.androidx.compose.bom))
                implementation(project.dependencies.platform(libs.koin.bom))

                api(libs.androidx.windowCore)
                api(libs.androidx.compose.ui.toolingPreview)
                api(libs.androidx.compose.material3.adaptive.layout)
                api(libs.androidx.compose.material3.adaptive.navigation)
                api(libs.androidx.compose.material3.adaptiveNavigationSuite)
                implementation(libs.androidx.compose.ui.util)
                implementation(libs.drick.compose.edgeToEdgePreview)

                api(libs.androidx.hilt.navigationCompose)
                api(libs.androidx.lifecycle.viewmodel)
                api(libs.androidx.paging.compose)

                api(libs.coil3.test)

                implementation(libs.koin.compose)
                implementation(libs.koin.android)
                implementation(libs.compose.destinations.core)
                implementation(libs.kotlinx.serialization.json)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.ui"
}

dependencies {
    add("kspAndroid", libs.compose.destinations.ksp)
}

ksp {
    arg("compose-destinations.codeGenPackageName", "com.sorrowblue.comicviewer.framework.ui}")
    arg("KOIN_CONFIG_CHECK", "false")
}
