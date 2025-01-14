plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.di)
    alias(libs.plugins.kotlinx.atomicfu)
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.common"
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
