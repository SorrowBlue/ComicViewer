plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
}

android {
    namespace = "com.sorrowblue.comicviewer.data.reader"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.squareup.okio)
            }
        }
    }
}
