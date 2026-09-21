plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.feature.permission"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.permission.nav)
                implementation(projects.framework.permission)
            }
        }
    }
}
