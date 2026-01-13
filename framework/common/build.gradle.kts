plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.comicviewer.di)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.framework.common"
    }
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.startupRuntime)
        }
    }
}
