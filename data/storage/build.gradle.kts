plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.data.storage"
    }
    sourceSets {
        commonMain {
            dependencies {
                api(projects.domain.service)
                implementation(libs.kotlinx.coroutinesCore)
                api(libs.okio)
            }
        }
        val androidJvm by creating {
            dependsOn(commonMain.get())
        }
        androidMain {
            dependsOn(androidJvm)
            dependencies {
                implementation(libs.androidx.startupRuntime)
            }
        }
        jvmMain {
            dependsOn(androidJvm)
        }
    }
}
