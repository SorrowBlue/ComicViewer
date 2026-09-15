plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.data.storage"
    }
    sourceSets {
        commonMain {
            dependencies {
                api(projects.domain.repository)
                implementation(projects.domain.service)
                api(libs.kotlinx.io)
            }
        }
    }
}
