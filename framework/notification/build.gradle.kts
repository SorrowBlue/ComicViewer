plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.framework.notification"
    }
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.startup.runtime)
            }
        }
    }
}
