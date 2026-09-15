plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.data.sync"
    }
    sourceSets {
        commonMain {
            dependencies {
                api(projects.domain.service)
                implementation(projects.domain.usecase)
                implementation(libs.kotlinx.coroutinesCore)
            }
        }
        androidMain {
            dependencies {
                implementation(projects.framework.background)
                implementation(projects.framework.notification)
                implementation(libs.androidx.workRuntime)
                implementation(libs.metro.android)
                implementation(libs.androidx.coreKtx)
            }
        }
        jvmMain {
            dependencies {
                implementation(projects.framework.notification)
            }
        }
        noAndroidTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutinesTest)
            }
        }
    }
}
