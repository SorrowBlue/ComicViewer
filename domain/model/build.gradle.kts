plugins {
    alias(libs.plugins.comicviewer.android.kotlinMultiplatform)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.sorrowblue.comicviewer.domain.model"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization.core)
            }
        }
    }
}
