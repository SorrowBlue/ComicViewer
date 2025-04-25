plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
}

kotlin {
    sourceSets {
        androidMain{
            dependencies {
                implementation(libs.androidx.test.ext.junitKtx)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.test"
}
