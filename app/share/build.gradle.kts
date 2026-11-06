import com.github.skydoves.navgraph.gradle.RenderBackend
import com.sorrowblue.comicviewer.configureManagedDevices
import com.sorrowblue.comicviewer.diSubprojects

plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.comicviewer.multiplatformCompose)
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.ksp)
    alias(libs.plugins.navgraph)
}

tasks.configureEach {
    if (name == "renderNavGraphGalleryLayoutlib" || name == "renderNavGraphLayoutlib") {
        // navgraphのlayoutlib描画がandroidAppのマージ済みassetsを参照するため、実行順依存を明示する
        dependsOn(":app:androidApp:mergeDebugAssets")
    }
}

tasks.withType<Test>().configureEach {
    if (name.contains("AndroidHostTest")) {
        filter {
            isFailOnNoMatchingTests = false
            excludeTestsMatching("com.sorrowblue.comicviewer.app.NavigationTest")
        }
    }
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.app"
        androidResources.enable = true
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "com.sorrowblue.comicviewer.app.InstrumentationTestRunner"
            execution = "ANDROIDX_TEST_ORCHESTRATOR"
            animationsDisabled = true
            instrumentationRunnerArguments["clearPackageData"] = "true"
            configureManagedDevices()
        }
        withHostTest { }
    }
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3ExpressiveApi",
            "-opt-in=androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi",
        )
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.domain.model)
                api(projects.domain.usecase)
                api(projects.framework.common)
                api(projects.framework.navigation)
                api(projects.framework.ui)
                implementation(projects.feature.bookshelf)
                implementation(projects.feature.folder.nav)
                implementation(projects.framework.designsystem)

                implementation(libs.androidx.navigation3UI)
                implementation(libs.compose.componentsResources)
                implementation(libs.compose.material3)
                implementation(libs.compose.material3AdaptiveLayout)
                implementation(libs.compose.material3AdaptiveNavigation3)
                implementation(libs.metro.viewmodelCompose)
                implementation(libs.kotlinx.collectionsImmutable)
                api(libs.metro.viewmodel)
            }
        }
        named("noAndroid") {
            dependencies {
                api(projects.framework.startup)
            }
        }
        commonTest.dependencies {
            diSubprojects.forEach(::implementation)
            implementation(projects.framework.test)
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutinesTest)
            implementation(libs.compose.uiTest)
            implementation(libs.filekit.core)
        }
        androidMain.dependencies {
            implementation(projects.feature.book)
            implementation(libs.metro.android)
            implementation(libs.androidx.workRuntime)
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.coreSplashscreen)
        }
        jvmMain.dependencies {
            implementation(libs.filekit.core)
            implementation(libs.compose.componentsAnimatedimage)
        }
        jvmTest.dependencies {
            implementation(libs.kotlinx.coroutinesSwing)
        }
        named("androidDeviceTest") {
            dependencies {
                runtimeOnly(libs.androidx.test.espressoCore)
            }
        }
    }
}

dependencies {
    androidTestUtil(libs.androidx.testOrchestrator)
}

buildConfig {
    packageName = "com.sorrowblue.comicviewer.app"
    buildConfigField("SMB_HOST", providers.gradleProperty("smbHost").orElse(""))
    buildConfigField(
        "SMB_PORT",
        providers.gradleProperty("smbPort").map { it.toIntOrNull() ?: 445 }.orElse(445),
    )
    buildConfigField("SMB_USERNAME", providers.gradleProperty("smbUsername").orElse(""))
    buildConfigField("SMB_DOMAIN", providers.gradleProperty("smbDomain").orElse(""))
    buildConfigField("SMB_PASSWORD", providers.gradleProperty("smbPassword").orElse(""))
    buildConfigField("SMB_PATH", providers.gradleProperty("smbPath").orElse(""))
}

ksp {
    arg("navgraph.annotatedOnly", "true")
}

navgraph {
    renderBackend.set(RenderBackend.LAYOUTLIB)
    galleryRenderBackend.set(RenderBackend.LAYOUTLIB)
}
