plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.framework.notification"
        androidResources.enable = true
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain.model)
        }
        androidMain {
            dependencies {
                implementation(projects.framework.startup)
                implementation(libs.androidx.coreKtx)
            }
        }
    }
}
