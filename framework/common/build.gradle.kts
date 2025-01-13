plugins {
    alias(libs.plugins.comicviewer.android.kotlinMultiplatform)
    id("org.jetbrains.kotlinx.atomicfu") version "0.27.0"
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.common"
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.google.dagger.hilt.android)
                implementation(libs.androidx.startup.runtime)
            }
        }
    }
}
