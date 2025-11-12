plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.di)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.domain.model)

                implementation(libs.kotlinx.datetime)
                implementation(libs.androidx.paging.common)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.domain.usecase"
    sourceSets {
        debug {
            manifest.srcFile("src/androidDebug/AndroidManifest.xml")
        }
    }
}

dependencies {
    debugImplementation(libs.kotlinx.serialization.json)
    debugImplementation(libs.androidx.appcompat)
    // Suppressing highlights in @Serializable
    debugImplementation(libs.androidx.annotation.experimental)
}
