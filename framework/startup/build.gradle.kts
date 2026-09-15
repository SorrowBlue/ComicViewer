plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.framework.startup"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain.model)
        }
        androidMain.dependencies {
            api(libs.androidx.startupRuntime)
        }
    }
}
