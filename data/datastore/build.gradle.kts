plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.data.datastore"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.domain.service)
                implementation(libs.androidx.datastoreCoreOkio)
                implementation(libs.filekit.core)
                implementation(libs.kotlinx.serializationProtobuf)
            }
        }
        androidMain {
            dependencies {
                implementation(projects.framework.startup)
                implementation(libs.androidx.appcompat)
            }
        }
    }
}
