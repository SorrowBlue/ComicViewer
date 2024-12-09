plugins {
    alias(libs.plugins.comicviewer.android.kotlinMultiplatform)
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
