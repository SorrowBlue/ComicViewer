plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.data.reader.document"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.data.storage)
                implementation(libs.kotlinx.coroutinesCore)
                implementation(libs.kpdfium)
            }
        }
    }
}
