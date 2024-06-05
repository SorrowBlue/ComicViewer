@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.sorrowblue.comicviewer.ComicBuildType
import com.sorrowblue.comicviewer.projectString
import org.jetbrains.kotlin.konan.properties.propertyString

plugins {
    id("comicviewer.android.application")
    id("comicviewer.android.application.compose")
    id("comicviewer.android.hilt")
    id("comicviewer.android.koin")
    alias(libs.plugins.mikepenz.aboutlibraries.plugin)
    alias(libs.plugins.arturbosch.detekt)
    alias(libs.plugins.grgit)
    alias(libs.plugins.kotlin.plugin.parcelize)
}

fun String.toVersion() = this + if (matches(".*-[0-9]+-g[0-9a-f]{7}".toRegex())) "-SNAPSHOT" else ""
android {
    namespace = "com.sorrowblue.comicviewer.app"
    defaultConfig {
        applicationId = "com.sorrowblue.comicviewer"
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 15
        versionName = grgitService.service.get().grgit.describe {
            longDescr = false
            isTags = true
        }?.toVersion() ?: "0.1.4-SNAPSHOT"
        logger.lifecycle("versionName=$versionName")
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    androidResources {
        generateLocaleConfig = true
    }
    signingConfigs {
        val localProperties = gradleLocalProperties(rootDir, providers)
        fun propertyString(name: String) =
            System.getenv(name) ?: localProperties.propertyString(name)

        val debugStoreFile = propertyString("debug_storeFile")?.let {
            if (it.isNotEmpty()) file(it) else null
        }
        if (debugStoreFile?.exists() == true) {
            getByName("debug") {
                storeFile = debugStoreFile
                storePassword = propertyString("debug_storePassword")
                keyAlias = propertyString("debug_keyAlias")
                keyPassword = propertyString("debug_keyPassword")
            }
        } else {
            logger.warn("debugStoreFile not found")
        }

        val releaseStoreFile = propertyString("release_storeFile")?.let { if (it.isNotEmpty()) file(it) else null }
        if (releaseStoreFile?.exists() == true) {
            val release = create("release") {
                storeFile = releaseStoreFile
                storePassword = propertyString("release_storePassword")
                keyAlias = propertyString("release_keyAlias")
                keyPassword = propertyString("release_keyPassword")
            }
            create("prerelease") {
                initWith(release)
            }
            create("internal") {
                initWith(release)
            }
        }
    }

    buildTypes {
        release {
            applicationIdSuffix = ComicBuildType.RELEASE.applicationIdSuffix
            isMinifyEnabled = ComicBuildType.RELEASE.isMinifyEnabled
            isShrinkResources = ComicBuildType.RELEASE.isShrinkResources
            signingConfig = signingConfigs.findByName(name)
        }
        getByName("prerelease") {
            applicationIdSuffix = ComicBuildType.PRERELEASE.applicationIdSuffix
            isMinifyEnabled = ComicBuildType.PRERELEASE.isMinifyEnabled
            isShrinkResources = ComicBuildType.PRERELEASE.isShrinkResources
            signingConfig = signingConfigs.findByName(name)
        }
        getByName("internal") {
            applicationIdSuffix = ComicBuildType.INTERNAL.applicationIdSuffix
            isMinifyEnabled = ComicBuildType.INTERNAL.isMinifyEnabled
            isShrinkResources = ComicBuildType.INTERNAL.isShrinkResources
            signingConfig = signingConfigs.findByName(name)
        }
        debug {
            applicationIdSuffix = ComicBuildType.DEBUG.applicationIdSuffix
            isMinifyEnabled = ComicBuildType.DEBUG.isMinifyEnabled
            isShrinkResources = ComicBuildType.DEBUG.isShrinkResources
            signingConfig = signingConfigs.findByName(name)
        }
    }

    dynamicFeatures += setOf(
        projects.data.reader.document.projectString(),
        projects.feature.library.box.projectString(),
        projects.feature.library.dropbox.projectString(),
        projects.feature.library.googledrive.projectString(),
        projects.feature.library.onedrive.projectString(),
    )
    buildFeatures {
        buildConfig = true
    }

    lint {
        checkDependencies = true
        htmlReport = true
        htmlOutput = file("$rootDir/build/reports/lint/lint-result.html")
        sarifReport = true
        sarifOutput = file("$rootDir/build/reports/lint/lint-result.sarif")
        textReport = false
        xmlReport = false
    }

    testOptions {
        managedDevices {
            localDevices {
                create("pixel8api34") {
                    device = "Pixel 8"
                    apiLevel = 34
                    systemImageSource = "aosp-atd"
                }
            }
        }
    }
}

dependencies {
    implementation(projects.framework.ui)
    implementation(projects.framework.notification)
    implementation(projects.framework.designsystem)

    implementation(projects.di)
    implementation(projects.domain.usecase)
    implementation(projects.feature.authentication)
    implementation(projects.feature.book)
    implementation(projects.feature.bookshelf)
    implementation(projects.feature.favorite)
    implementation(projects.feature.favorite.add)
    implementation(projects.feature.readlater)
    implementation(projects.feature.search)
    implementation(projects.feature.settings)
    implementation(projects.feature.settings.security)
    implementation(projects.feature.tutorial)
    implementation(projects.feature.library)

    implementation(libs.androidx.compose.material3.adaptiveNavigationSuite)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.hilt.work)
    implementation(libs.google.android.play.review.ktx)
    implementation(libs.google.android.play.feature.delivery.ktx)
    implementation(libs.androidx.appcompat)

    implementation(libs.google.android.billingclient.billingKtx)

    debugImplementation(libs.squareup.leakcanary.android)

    androidTestImplementation(libs.androidx.test.core.ktx)
    androidTestImplementation(libs.androidx.test.ext.truth)
    androidTestImplementation(libs.androidx.test.ext.junitKtx)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.espresso.core)
}

/*
abstract class BuildApksTask : DefaultTask() {

    @get:InputFile
    abstract val bundletool: RegularFileProperty

    @get:InputFile
    abstract val bundle: RegularFileProperty

    @get:InputFile
    abstract val debugStoreFile: RegularFileProperty

    @get:OutputFile
    abstract val output: RegularFileProperty

    @get:Inject
    abstract val eo: ExecOperations

    init {
        group = "comicviewer"
    }

    @TaskAction
    fun action() {
        org.apache.commons.io.output.ByteArrayOutputStream().use { stdout ->
            eo.exec {
                commandLine(
                    "cmd",
                    "/c",
                    "java",
                    "-jar",
                    "${bundletool.get()}",
                    "build-apks",
                    "--local-testing",
                    "--bundle=${bundle.get()}",
                    "--output=${output.get()}",
                    "--overwrite",
                    "--ks=${debugStoreFile.get()}",
                    "--ks-pass=pass:android",
                    "--ks-key-alias=androiddebugkey",
                    "--key-pass=pass:android"
                )
            }
            logger.lifecycle(stdout.toString().trim())
        }
    }
}

abstract class InstallApksTask : DefaultTask() {

    @get:InputFile
    abstract val bundletool: RegularFileProperty

    @get:InputFile
    abstract val output: RegularFileProperty

    @get:Inject
    abstract val eo: ExecOperations

    init {
        group = "comicviewer"
    }

    @TaskAction
    fun action() {
        org.apache.commons.io.output.ByteArrayOutputStream().use { stdout ->
            eo.exec {
                commandLine(
                    "cmd",
                    "/c",
                    "java",
                    "-jar",
                    bundletool.get(),
                    "install-apks",
                    "--apks=${output.get()}"
                )
            }
            logger.lifecycle(stdout.toString().trim())
            eo.exec {
                commandLine(
                    "cmd",
                    "/c",
                    "adb",
                    "shell",
                    "am",
                    "start",
                    "-n",
                    "com.sorrowblue.comicviewer.debug/com.sorrowblue.comicviewer.app.MainComposeActivity"
                )
            }
            logger.lifecycle(stdout.toString().trim())
        }
    }
}
tasks.register<BuildApksTask>("buildApksDebug") {
    dependsOn("bundleDebug")
    val projectDir = layout.projectDirectory
    bundletool.set(File(gradleLocalProperties(rootDir).getProperty("bundletool")))
    debugStoreFile.set(File(gradleLocalProperties(rootDir).getProperty("debug.storeFile")))
    bundle.set(projectDir.file("build/outputs/bundle/debug/app-debug.aab"))
    output.set(projectDir.file("build/outputs/bundle/debug/app-debug.apks"))
}

tasks.register<InstallApksTask>("installApksDebug") {
    dependsOn("buildApksDebug")
    val projectDir = layout.projectDirectory
    bundletool.set(File(gradleLocalProperties(rootDir).getProperty("bundletool")))
    output.set(projectDir.file("build/outputs/bundle/debug/app-debug.apks"))
}
*/
