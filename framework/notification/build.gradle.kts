plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.framework.notification"
        androidResources.enable = true
    }

    sourceSets {
        androidMain {
            dependencies {
                implementation(projects.framework.startup)
                implementation(libs.androidx.coreKtx)
            }
        }
    }
}
