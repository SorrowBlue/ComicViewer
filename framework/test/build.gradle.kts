plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.framework.test"
    }
    sourceSets {
        androidMain {
            dependencies {
                api(libs.androidx.testExtJunit)
                api(libs.androidx.testRunner)
                implementation(libs.junit)
            }
        }
        jvmMain {
            dependencies {
                implementation(kotlin("test-junit5"))
            }
        }
    }
}
