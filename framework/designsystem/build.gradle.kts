plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.compose)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.materialIconsExtended)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.appcompat)
                implementation(libs.koin.core)
            }
        }
        noAndroid {
            dependencies {
                implementation(projects.domain.usecase)
                implementation(libs.koin.composeViewModel)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.designsystem"
}
