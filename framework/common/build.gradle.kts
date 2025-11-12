plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.di)
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.androidx.startup.runtime)
            }
        }
        commonMain {
            dependencies {
                implementation(libs.androidx.compose.runtime)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.common"
}
