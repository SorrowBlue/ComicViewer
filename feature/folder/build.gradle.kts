plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.feature.folder"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.folder.nav)
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
