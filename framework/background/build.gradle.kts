plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.framework.background"
    }
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.workRuntime)
        }
    }
}
