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
                implementation(libs.compose.multiplatform.material3.adaptive)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.appcompat)
                api(project.dependencies.platform(libs.androidx.compose.bom))
            }
        }

        noAndroid.dependencies {
            implementation(projects.domain.usecase)
            implementation(libs.koin.composeViewModel)
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.designsystem"
}
