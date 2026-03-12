plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.framework.common"
    }
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.startupRuntime)
        }
    }
}
