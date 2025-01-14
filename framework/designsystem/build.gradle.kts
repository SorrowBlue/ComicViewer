plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.compose)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project.dependencies.platform(libs.androidx.compose.bom))
                api(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.components.uiToolingPreview)
                implementation(compose.material3AdaptiveNavigationSuite)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.compose.material3.adaptive.layout)
                implementation(libs.androidx.appcompat)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.designsystem"
}

dependencies {
    debugImplementation(compose.uiTooling)
}
