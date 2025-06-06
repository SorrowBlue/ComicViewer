plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
    alias(libs.plugins.androidx.room)
}

android {
    namespace = "com.sorrowblue.comicviewer.data.database"

    sourceSets {
        getByName("test") {
            assets.srcDir(file("$projectDir/schemas"))
        }
    }

    @Suppress("UnstableApiUsage")
    testOptions {
        managedDevices {
            localDevices {
                create("pixel9api35") {
                    // Use device profiles you typically see in Android Studio.
                    device = "Pixel 9"
                    // Use only API levels 27 and higher.
                    apiLevel = 35
                    // To include Google services, use "google".
                    systemImageSource = "aosp-atd"
                }
            }
        }
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-opt-in=com.sorrowblue.comicviewer.domain.model.ExperimentalIdValue")
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
                implementation(libs.kotlinx.serialization.json)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.framework.test)
                implementation(libs.kotlin.test)
                implementation(libs.koin.test)
                implementation(libs.androidx.room.testing)
                implementation(libs.kotlinx.coroutines.test)
            }
        }

        desktopMain {
            dependencies {
                implementation(libs.credential.secure.storage)
            }
        }

        androidUnitTest {
            dependencies {
                implementation(libs.androidx.test.coreKtx)
                implementation(libs.androidx.test.runner)
                implementation(libs.androidx.test.rules)
                implementation(libs.androidx.test.ext.junitKtx)
                implementation(libs.robolectric)
            }
        }

        androidInstrumentedTest {
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
    kspAndroidTest(libs.androidx.room.compiler)
    kspDesktop(libs.androidx.room.compiler)
    kspDesktopTest(libs.androidx.room.compiler)
    kspIosArm64(libs.androidx.room.compiler)
    kspIosArm64Test(libs.androidx.room.compiler)
    kspIosSimulatorArm64(libs.androidx.room.compiler)
    kspIosSimulatorArm64Test(libs.androidx.room.compiler)
    kspIosX64(libs.androidx.room.compiler)
    kspIosX64Test(libs.androidx.room.compiler)
}

ksp {
    arg("room.generateKotlin", "true")
}

room {
    schemaDirectory("$projectDir/schemas")
}
