plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.data.storage"
    }
    sourceSets {
        commonMain {
            dependencies {
                api(projects.domain.service)
                implementation(libs.kotlinx.coroutinesCore)
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
