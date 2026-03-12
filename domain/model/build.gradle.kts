plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.domain.model"
    }
    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serializationCore)
            }
        }
    }
}
