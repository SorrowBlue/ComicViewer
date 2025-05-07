plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
    alias(libs.plugins.androidx.room)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.data.database"
    }
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

        val androidHostTest by getting {
            dependencies {
                implementation(projects.framework.test)
                implementation(libs.androidx.test.core.ktx)
                implementation(libs.androidx.test.runner)
                implementation(libs.androidx.test.rules)
                implementation(libs.androidx.test.ext.junitKtx)
                implementation(libs.robolectric)
            }
        }
        val androidDeviceTest by getting {
            dependencies {
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
}

dependencies {
    add("kspIosX64", libs.androidx.room.compiler)
    add("kspIosX64Test", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosArm64Test", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64Test", libs.androidx.room.compiler)
    add("kspDesktop", libs.androidx.room.compiler)
    add("kspDesktopTest", libs.androidx.room.compiler)
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspAndroidHostTest", libs.androidx.room.compiler)
}

ksp {
    arg("room.generateKotlin", "true")
}

room {
    schemaDirectory("$projectDir/schemas")
}

androidComponents {
    onVariant { variant ->
        variant.nestedComponents.forEach { nest ->
            nest.sources.assets?.addStaticSourceDirectory("$projectDir/schemas")
        }
    }
}
