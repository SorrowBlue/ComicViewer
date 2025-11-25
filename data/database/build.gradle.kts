plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.comicviewer.di)
    alias(libs.plugins.androidxRoom)
    alias(libs.plugins.ksp)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.data.database"
        withHostTest {
            isIncludeAndroidResources = true
        }
        withDeviceTest {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            execution = "ANDROIDX_TEST_ORCHESTRATOR"
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

    compilerOptions {
        freeCompilerArgs.add("-opt-in=androidx.paging.ExperimentalPagingApi")
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.domain.service)
                implementation(libs.androidx.room.runtime)
                implementation(libs.androidx.room.paging)
                implementation(libs.androidx.sqlite.bundled)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serializationJson)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.framework.test)
                implementation(libs.kotlin.test)
                implementation(libs.androidx.pagingTesting)
                implementation(libs.androidx.room.testing)
                implementation(libs.kotlinx.coroutines.test)
            }
        }

        getByName("androidHostTest") {
            dependencies {
                implementation(libs.androidx.test.coreKtx)
                implementation(libs.androidx.test.runner)
                implementation(libs.androidx.test.rules)
                implementation(libs.androidx.test.ext.junitKtx)
                implementation(libs.robolectric)
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.test.coreKtx)
                implementation(libs.androidx.test.runner)
                implementation(libs.androidx.test.rules)
                implementation(libs.androidx.test.ext.junitKtx)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.androidx.room.testing)
                implementation(libs.androidx.sqlite.bundled)
            }
        }
    }
}

dependencies {
    kspAndroid(libs.androidx.room.compiler)
    add("kspAndroidHostTest", libs.androidx.room.compiler)
    add("kspAndroidDeviceTest", libs.androidx.room.compiler)
    kspDesktop(libs.androidx.room.compiler)
    kspDesktopTest(libs.androidx.room.compiler)
    kspIosArm64(libs.androidx.room.compiler)
    kspIosArm64Test(libs.androidx.room.compiler)
    kspIosSimulatorArm64(libs.androidx.room.compiler)
    kspIosSimulatorArm64Test(libs.androidx.room.compiler)
    kspIosX64(libs.androidx.room.compiler)
    kspIosX64Test(libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}
