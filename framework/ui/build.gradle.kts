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
                api(projects.framework.navigation.annotations)

                implementation(compose.material3)
                implementation("org.jetbrains.compose.material3.adaptive:adaptive:1.1.0-alpha02")
                implementation("org.jetbrains.compose.material3.adaptive:adaptive-layout:1.1.0-alpha02")
                api("org.jetbrains.compose.material3.adaptive:adaptive-navigation:1.1.0-alpha02")
                implementation("org.jetbrains.compose.material3:material3-window-size-class:1.7.3")
                implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha12")
                implementation(compose.material3AdaptiveNavigationSuite)

                implementation(compose.components.uiToolingPreview)
                implementation(compose.uiUtil)

                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.serialization.jsonOkio)
                implementation(libs.androidx.paging.common)
                implementation(libs.androidx.lifecycle.viewmodel)
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

                api(libs.androidx.lifecycle.viewmodel)
                api(libs.androidx.paging.compose)

                api(libs.coil3.test)

                implementation(libs.koin.compose)
                implementation(libs.koin.android)
                implementation(libs.kotlinx.serialization.json)
            }
        }

        val noAndroid by getting {
            dependencies {
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.composeViewModel)
                implementation(libs.koin.composeViewModelNavigation)
            }
        }
    }
}

compose.resources {
    publicResClass = true
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.ui"
}

ksp {
    arg("KOIN_CONFIG_CHECK", "false")
}
