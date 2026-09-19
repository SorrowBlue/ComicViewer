plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.framework.startup"
    }
    sourceSets {
        androidMain.dependencies {
            api(libs.androidx.startupRuntime)
        }
    }
}
