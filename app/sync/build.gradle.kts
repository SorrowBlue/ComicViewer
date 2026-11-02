plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.app.sync"
    }
    sourceSets {
        commonMain {
            dependencies {
                api(projects.domain.repository)
                implementation(projects.domain.usecase)
            }
        }
        androidMain {
            dependencies {
                implementation(projects.framework.background)
                implementation(projects.framework.notification)
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
