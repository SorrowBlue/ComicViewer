plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.file)
                implementation(projects.feature.folder)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.history"
    resourcePrefix("history")
}
