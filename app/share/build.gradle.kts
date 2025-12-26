plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.comicviewer.multiplatformCompose)
    alias(libs.plugins.comicviewer.di)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.app.share"
        androidResources.enable = true
        withDeviceTest {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            execution = "ANDROIDX_TEST_ORCHESTRATOR"
            animationsDisabled = true
            instrumentationRunnerArguments["clearPackageData"] = "true"
            managedDevices {
                localDevices {
                    create("pixel9api35") {
                        device = "Pixel 9"
                        apiLevel = 35
                        systemImageSource = "aosp-atd"
                    }
                }
            }
        }
    }
    sourceSets {
        commonMain {
            dependencies {
                rootProject.subprojects.filterNot {
                    it.path == project.path || it.path.startsWith(projects.app.path)
                        || it.path == projects.data.reader.document.android.path
                        || it.path == projects.framework.notification.path
                }.forEach {
                    val hasSource = it.projectDir.resolve("src").exists()
                    if (hasSource) {
                        implementation(it)
                    } else {
                        logger.lifecycle("Skipping empty or non-source module: ${it.path}")
                    }
                }
                // Required for metro dependency resolution
                implementation(libs.androidx.datastore)
                implementation(projects.data.reader.zip)
            }
        }
        androidMain.dependencies {
            implementation(projects.data.reader.document.android)
            implementation(libs.metro.android)
        }
        val androidDeviceTest by getting {
            dependencies {
                implementation(libs.compose.uiTestJunit4)
                implementation(libs.compose.uiTestManifest)
            }
        }
    }
}

dependencies {
    androidTestUtil("androidx.test:orchestrator:1.6.1")
}
