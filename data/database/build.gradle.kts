plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.comicviewer.di)
    alias(libs.plugins.androidxRoom)
    alias(libs.plugins.ksp)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.data.database"
        androidResources.enable = true
        withHostTest {
            isIncludeAndroidResources = true
        }
        withDeviceTest {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//            execution = "ANDROIDX_TEST_ORCHESTRATOR"
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
                implementation(libs.androidx.roomRuntime)
                implementation(libs.androidx.roomPaging)
                implementation(libs.androidx.sqliteBundled)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serializationJson)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.framework.test)
                implementation(libs.kotlin.test)
                implementation(libs.androidx.pagingTesting)
                implementation(libs.androidx.roomTesting)
                implementation(libs.kotlinx.coroutinesTest)
            }
        }

        getByName("androidHostTest") {
            dependencies {
                implementation(libs.androidx.testCoreKtx)
                implementation(libs.androidx.testRunner)
                implementation(libs.androidx.testRules)
                implementation(libs.androidx.testExtJunitKtx)
                implementation(libs.robolectric)
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.testCoreKtx)
                implementation(libs.androidx.testRunner)
                implementation(libs.androidx.testRules)
                implementation(libs.androidx.testExtJunitKtx)
                implementation(libs.kotlinx.coroutinesTest)
                implementation(libs.androidx.roomTesting)
                implementation(libs.androidx.sqliteBundled)
            }
        }
    }
}

dependencies {
    kspAndroid(libs.androidx.roomCompiler)
    add("kspAndroidHostTest", libs.androidx.roomCompiler)
    add("kspAndroidDeviceTest", libs.androidx.roomCompiler)
    kspDesktop(libs.androidx.roomCompiler)
    kspDesktopTest(libs.androidx.roomCompiler)
    kspIosArm64(libs.androidx.roomCompiler)
    kspIosArm64Test(libs.androidx.roomCompiler)
    kspIosSimulatorArm64(libs.androidx.roomCompiler)
    kspIosSimulatorArm64Test(libs.androidx.roomCompiler)
    kspIosX64(libs.androidx.roomCompiler)
    kspIosX64Test(libs.androidx.roomCompiler)
    androidTestUtil(libs.androidx.testOrchestrator)
}

room {
    schemaDirectory("$projectDir/schemas")
}

afterEvaluate {
    tasks.getByName("lintAnalyzeAndroidHostTest") {
        dependsOn(tasks.getByName("kspAndroidHostTest"))
    }
    tasks.getByName("generateAndroidHostTestLintModel") {
        dependsOn(tasks.getByName("kspAndroidHostTest"))
    }
}
tasks.withType<AbstractTestTask>().configureEach {
    failOnNoDiscoveredTests = false
}
