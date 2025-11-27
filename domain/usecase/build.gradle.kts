plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.comicviewer.di)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.domain.usecase"
        this.compilations
//        sourceSets {
//            debug {
//                manifest.srcFile("src/androidDebug/AndroidManifest.xml")
//            }
//        }
    }
    sourceSets {
        commonMain {
            dependencies {
                api(projects.domain.model)

                implementation(libs.kotlinx.datetime)
                implementation(libs.androidx.pagingCommon)
            }
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.kotlinx.serializationJson)
    androidRuntimeClasspath(libs.androidx.appcompat)
    // Suppressing highlights in @Serializable
    androidRuntimeClasspath(libs.androidx.annotation.experimental)
}
