import com.sorrowblue.comicviewer.desktopMain

plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
    alias(libs.plugins.androidx.room)
}

android {
    namespace = "com.sorrowblue.comicviewer.data.database"

    sourceSets {
        val test by getting
        test.assets.srcDir(file("$projectDir/schemas"))
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
                implementation(libs.androidx.paging.common)
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

        desktopMain.dependencies {
            implementation("com.microsoft:credential-secure-storage:1.0.0")
        }

        androidUnitTest.dependencies {
            implementation(libs.androidx.test.core.ktx)
            implementation(libs.androidx.test.runner)
            implementation(libs.androidx.test.rules)
            implementation(libs.androidx.test.ext.junitKtx)
            implementation(libs.robolectric)
        }

        androidInstrumentedTest.dependencies {
            implementation(libs.androidx.test.core.ktx)
            implementation(libs.androidx.test.runner)
            implementation(libs.androidx.test.rules)
            implementation(libs.androidx.test.ext.junitKtx)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.androidx.room.testing)
            implementation(libs.androidx.sqlite.bundled)
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", libs.androidx.room.compiler)
    add("kspIosX64", libs.androidx.room.compiler)
    add("kspIosX64Test", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosArm64Test", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64Test", libs.androidx.room.compiler)
    add("kspDesktop", libs.androidx.room.compiler)
    add("kspDesktopTest", libs.androidx.room.compiler)
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspAndroidTest", libs.androidx.room.compiler)
}

ksp {
    arg("room.generateKotlin", "true")
}

room {
    schemaDirectory("$projectDir/schemas")
}
