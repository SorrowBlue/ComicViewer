@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.sorrowblue.comicviewer.ComicBuildType
import com.sorrowblue.comicviewer.projectString
import org.jetbrains.kotlin.konan.properties.propertyString

plugins {
    id("comicviewer.android.application")
    id("comicviewer.android.application.compose")
    id("comicviewer.android.hilt")
    alias(libs.plugins.mikepenz.aboutlibraries.plugin)
    alias(libs.plugins.arturbosch.detekt)
    alias(libs.plugins.grgit)
}

fun String.toVersion() = this + if (matches(".*-[0-9]+-g[0-9a-f]{7}".toRegex())) "-SNAPSHOT" else ""
android {
    namespace = "com.sorrowblue.comicviewer.app"
    defaultConfig {
        applicationId = "com.sorrowblue.comicviewer"
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 12
        versionName = grgit.describe {
            longDescr = false
            isTags = true
        }?.toVersion() ?: "0.0.1-SNAPSHOT"
        logger.lifecycle("versionName=$versionName")
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
    androidResources {
        generateLocaleConfig = true
    }

    signingConfigs {
        getByName("debug") {
            storeFile =
                file(gradleLocalProperties(rootDir).propertyString("debug.storeFile").orEmpty())
            storePassword = gradleLocalProperties(rootDir).propertyString("debug.storePassword")
            keyAlias = gradleLocalProperties(rootDir).propertyString("debug.keyAlias")
            keyPassword = gradleLocalProperties(rootDir).propertyString("debug.keyPassword")
        }
        create("release") {
            storeFile =
                file(gradleLocalProperties(rootDir).propertyString("release.storeFile").orEmpty())
            storePassword = gradleLocalProperties(rootDir).propertyString("release.storePassword")
            keyAlias = gradleLocalProperties(rootDir).propertyString("release.keyAlias")
            keyPassword = gradleLocalProperties(rootDir).propertyString("release.keyPassword")
        }
        create("prerelease") {
            storeFile =
                file(gradleLocalProperties(rootDir).propertyString("release.storeFile").orEmpty())
            storePassword = gradleLocalProperties(rootDir).propertyString("release.storePassword")
            keyAlias = gradleLocalProperties(rootDir).propertyString("release.keyAlias")
            keyPassword = gradleLocalProperties(rootDir).propertyString("release.keyPassword")
        }
        create("internal") {
            storeFile =
                file(gradleLocalProperties(rootDir).propertyString("release.storeFile").orEmpty())
            storePassword = gradleLocalProperties(rootDir).propertyString("release.storePassword")
            keyAlias = gradleLocalProperties(rootDir).propertyString("release.keyAlias")
            keyPassword = gradleLocalProperties(rootDir).propertyString("release.keyPassword")
        }
    }

    buildTypes {
        release {
            applicationIdSuffix = ComicBuildType.RELEASE.applicationIdSuffix
            isMinifyEnabled = ComicBuildType.RELEASE.isMinifyEnabled
            isShrinkResources = ComicBuildType.RELEASE.isShrinkResources
            signingConfig = signingConfigs.getByName(name)
        }
        getByName("prerelease") {
            applicationIdSuffix = ComicBuildType.PRERELEASE.applicationIdSuffix
            isMinifyEnabled = ComicBuildType.PRERELEASE.isMinifyEnabled
            isShrinkResources = ComicBuildType.PRERELEASE.isShrinkResources
            signingConfig = signingConfigs.getByName(name)
        }
        getByName("internal") {
            applicationIdSuffix = ComicBuildType.INTERNAL.applicationIdSuffix
            isMinifyEnabled = ComicBuildType.INTERNAL.isMinifyEnabled
            isShrinkResources = ComicBuildType.INTERNAL.isShrinkResources
            signingConfig = signingConfigs.getByName(name)
        }
        debug {
            applicationIdSuffix = ComicBuildType.DEBUG.applicationIdSuffix
            isMinifyEnabled = ComicBuildType.DEBUG.isMinifyEnabled
            isShrinkResources = ComicBuildType.DEBUG.isShrinkResources
            signingConfig = signingConfigs.getByName(name)
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
}

dependencies {
    implementation(projects.framework.ui)
    implementation(projects.framework.resource)
    implementation(projects.framework.notification)
    implementation(projects.framework.designsystem)

    implementation(projects.di)
    implementation(projects.domain.usecase)
    implementation(projects.feature.authentication)
    implementation(projects.feature.book)
    implementation(projects.feature.bookshelf)
    implementation(projects.feature.favorite)
    implementation(projects.feature.favorite.create)
    implementation(projects.feature.file.info)
    implementation(projects.feature.readlater)
    implementation(projects.feature.search)
    implementation(projects.feature.settings)
    implementation(projects.feature.settings.security)
    implementation(projects.feature.tutorial)
    implementation(projects.feature.library)

    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.hilt.navigationCompose)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.hilt.work)
    implementation(libs.google.android.play.review.ktx)
    implementation(libs.google.android.play.feature.delivery.ktx)
    implementation(libs.coil.compose)
    implementation(libs.mikepenz.aboutlibraries)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.google.accompanist.navigation.material)
    implementation(libs.kotlinx.collections.immutable)

//    debugImplementation(libs.squareup.leakcanary.android)

    androidTestImplementation(libs.androidx.test.ext.junit.ktx)
    androidTestImplementation(libs.androidx.test.espresso.core)
}

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
