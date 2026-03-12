plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.framework.test"
    }
    sourceSets {
        androidMain{
            dependencies {
                api(libs.androidx.testExtJunitKtx)
            }
        }
    }
}
