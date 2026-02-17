plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
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
