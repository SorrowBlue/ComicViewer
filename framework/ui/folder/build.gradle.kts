plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.framework.ui.folder"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.framework.permission)
                implementation(projects.framework.ui.file)
            }
        }
    }
}

compose {
    resources {
        publicResClass = true
    }
}
