plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
}

android {
    namespace = "com.sorrowblue.comicviewer.domain.reader"
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
