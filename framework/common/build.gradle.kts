plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.framework.common"
    }
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.androidx.startup.runtime)
            }
        }
    }
}
