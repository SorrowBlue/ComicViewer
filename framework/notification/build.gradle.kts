plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.startup.runtime)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.notification"
}
