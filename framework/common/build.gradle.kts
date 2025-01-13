plugins {
    alias(libs.plugins.comicviewer.android.kotlinMultiplatform)
    id("org.jetbrains.kotlinx.atomicfu") version "0.27.0"
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.common"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.domain.model)

                implementation(libs.androidx.paging.common)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.startup.runtime)
            }
        }
    }
}
