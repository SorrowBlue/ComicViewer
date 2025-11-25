plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)

    alias(libs.plugins.comicviewer.multiplatformCompose)
    alias(libs.plugins.comicviewer.di)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.framework.designsystem"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.compose.materialIconsExtended)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.appcompat)
            }
        }
        noAndroid {
            dependencies {
                implementation(projects.domain.usecase)
            }
        }
    }
}
