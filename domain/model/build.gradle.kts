plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.domain.model"
    }
    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization.core)
            }
        }
    }
}
