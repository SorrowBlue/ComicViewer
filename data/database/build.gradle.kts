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
//            execution = "ANDROIDX_TEST_ORCHESTRATOR"
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
                implementation(libs.androidx.room3Runtime)
                implementation(libs.androidx.room3Paging)
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
                implementation(libs.androidx.room3Testing)
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
                implementation(libs.androidx.room3Testing)
                implementation(libs.androidx.room3SqliteWrapper)
                implementation(libs.androidx.sqliteBundled)
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
    androidTestUtil(libs.androidx.testOrchestrator)
}

room3 {
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
