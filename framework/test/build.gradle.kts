plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.framework.test"
    }
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.androidx.test.ext.junitKtx)
            }
        }
    }
}
