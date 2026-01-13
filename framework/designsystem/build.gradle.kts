import com.sorrowblue.comicviewer.libs

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
                implementation(libs.compose.material3)
                implementation(libs.compose.material3Adaptive)
                implementation(libs.compose.materialIconsExtended)
                implementation(libs.rin)
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
