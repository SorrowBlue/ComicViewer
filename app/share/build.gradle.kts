import com.codingfeline.buildkonfig.compiler.FieldSpec
import com.sorrowblue.comicviewer.libs

plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.comicviewer.multiplatformCompose)
    alias(libs.plugins.comicviewer.di)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.buildkonfig)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.app.share"
        androidResources.enable = true
        withDeviceTest {
            instrumentationRunner = "com.sorrowblue.comicviewer.app.InstrumentationTestRunner"
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
                }.forEach {
                    val hasSource = it.projectDir.resolve("src").exists()
                    if (hasSource) {
                        api(it)
                    } else {
                        logger.lifecycle("Skipping empty or non-source module: ${it.path}")
                    }
                }
                // Required for metro dependency resolution
                implementation(libs.androidx.datastore)
                implementation(projects.data.reader.zip)

                implementation(libs.androidx.navigation3Runtime)
                implementation(libs.androidx.lifecycleCompose)
                implementation(libs.androidx.lifecycleViewmodelCompose)
                implementation(libs.androidx.lifecycleViewmodelNavigation3)
                implementation(libs.compose.material3)
                implementation(libs.compose.material3AdaptiveLayout)
                implementation(libs.compose.material3AdaptiveNavigation3)
                implementation(libs.compose.material3AdaptiveNavigationSuite)
                implementation(libs.rin)
                implementation(libs.navigation3.resultstate)
            }
        }
        androidMain.dependencies {
            api(projects.data.reader.document.android)
            implementation(libs.metro.android)
            implementation(libs.androidx.workRuntime)
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.coreSplashscreen)
        }
        val androidDeviceTest by getting {
            dependencies {
                implementation(libs.androidx.testRunner)
                implementation(libs.compose.uiTestJunit4)
                implementation(libs.compose.uiTestManifest)
            }
        }
    }
}

dependencies {
    androidTestUtil("androidx.test:orchestrator:1.6.1")
}

buildkonfig {
    packageName = "com.sorrowblue.comicviewer.app"
    objectName = "BuildTestConfig"
    defaultConfigs {
        buildConfigField(
            FieldSpec.Type.STRING,
            "smbHost",
            project.findProperty("smbHost")?.toString().orEmpty()
        )
        buildConfigField(
            FieldSpec.Type.INT,
            "smbPort",
            project.findProperty("smbPort")?.toString()?.toIntOrNull()?.toString() ?: "445"
        )
        buildConfigField(
            FieldSpec.Type.STRING,
            "smbUsername",
            project.findProperty("smbUsername")?.toString().orEmpty()
        )
        buildConfigField(
            FieldSpec.Type.STRING,
            "smbDomain",
            project.findProperty("smbDomain")?.toString().orEmpty()
        )
        buildConfigField(
            FieldSpec.Type.STRING,
            "smbPassword",
            project.findProperty("smbPassword")?.toString().orEmpty()
        )
        buildConfigField(
            FieldSpec.Type.STRING,
            "smbPath",
            project.findProperty("smbPath")?.toString().orEmpty()
        )
    }
}
