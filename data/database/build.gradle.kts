plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.androidxRoom3)
    alias(libs.plugins.ksp)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.data.database"
        androidResources.enable = true
        withHostTest {
            isIncludeAndroidResources = true
        }
        withDeviceTest {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            managedDevices {
                localDevices {
                    @Suppress("UnstableApiUsage")
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
                implementation(libs.androidx.pagingCommon)
                implementation(libs.androidx.room3Paging)
                implementation(libs.androidx.room3Runtime)
                implementation(libs.androidx.sqliteBundled)
                implementation(libs.kotlinx.datetime)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.framework.test)
                implementation(libs.kotlin.test)
                implementation(libs.androidx.pagingTesting)
                implementation(libs.androidx.room3Testing)
                implementation(libs.kotlinx.coroutinesTest)
            }
        }

        getByName("androidHostTest") {
            dependencies {
                runtimeOnly(libs.robolectric)
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.testRunner)
                implementation(libs.androidx.room3SqliteWrapper)
                implementation(libs.androidx.room3Testing)
                implementation(libs.kotlinx.coroutinesTest)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.filekit.core)
            }
        }
    }
}

dependencies {
    kspAndroid(libs.androidx.room3Compiler)
    add("kspAndroidHostTest", libs.androidx.room3Compiler)
    add("kspAndroidDeviceTest", libs.androidx.room3Compiler)
    kspJvm(libs.androidx.room3Compiler)
    kspJvmTest(libs.androidx.room3Compiler)
    kspIosArm64(libs.androidx.room3Compiler)
    kspIosArm64Test(libs.androidx.room3Compiler)
    kspIosSimulatorArm64(libs.androidx.room3Compiler)
    kspIosSimulatorArm64Test(libs.androidx.room3Compiler)
}

room3 {
    schemaDirectory("$projectDir/schemas")
}
