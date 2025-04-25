plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.file)
            }
        }
    }
}

compose {
    resources {
        publicResClass = true
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.folder"
    resourcePrefix("folder")
}
