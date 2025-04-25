plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.androidx.startup.runtime)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.common"
}
