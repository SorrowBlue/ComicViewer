plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.framework.test"
    }
    sourceSets {
        androidMain{
            dependencies {
                implementation(libs.androidx.test.ext.junitKtx)
            }
        }
    }
}
