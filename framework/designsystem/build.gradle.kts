plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.compose)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.compose.multiplatform.material3.adaptive)
            }
        }

        androidMain {
            dependencies {
                api(project.dependencies.platform(libs.androidx.compose.bom))
                implementation(libs.androidx.appcompat)
                implementation(libs.androidx.compose.material3.adaptive.layout)

            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.designsystem"
}
