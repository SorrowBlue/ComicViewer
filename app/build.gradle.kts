@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

import com.sorrowblue.comicviewer.ComicBuildType

plugins {
    alias(libs.plugins.comicviewer.android.application)
    alias(libs.plugins.comicviewer.android.compose)
    alias(libs.plugins.comicviewer.android.hilt)
    alias(libs.plugins.comicviewer.koin)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.aboutlibraries)
    alias(libs.plugins.detekt)
    alias(libs.plugins.semver)
}

android {
    namespace = "com.sorrowblue.comicviewer.app"
    defaultConfig {
        applicationId = "com.sorrowblue.comicviewer"
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = semver.gitDescribed.toString()
        logger.lifecycle("versionName=$versionName")
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
    androidResources {
        generateLocaleConfig = true
    }
    signingConfigs {
        val androidSigningDebugStoreFile: String? by project
        if (androidSigningDebugStoreFile?.isNotEmpty() == true && file(androidSigningDebugStoreFile!!).exists()) {
            getByName("debug") {
                val androidSigningDebugStorePassword: String? by project
                val androidSigningDebugKeyAlias: String? by project
                val androidSigningDebugKeyPassword: String? by project
                storeFile = file(androidSigningDebugStoreFile!!)
                storePassword = androidSigningDebugStorePassword
                keyAlias = androidSigningDebugKeyAlias
                keyPassword = androidSigningDebugKeyPassword
            }
        } else {
            logger.warn("debugStoreFile not found")
        }

        val androidSigningReleaseStoreFile: String? by project
        if (androidSigningReleaseStoreFile?.isNotEmpty() == true && file(
                androidSigningReleaseStoreFile!!
            ).exists()
        ) {
            val release = create("release") {
                val androidSigningReleaseStorePassword: String? by project
                val androidSigningReleaseKeyAlias: String? by project
                val androidSigningReleaseKeyPassword: String? by project
                storeFile = file(androidSigningReleaseStoreFile!!)
                storePassword = androidSigningReleaseStorePassword
                keyAlias = androidSigningReleaseKeyAlias
                keyPassword = androidSigningReleaseKeyPassword
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
        projects.data.reader.document.path,
        projects.feature.library.box.path,
        projects.feature.library.dropbox.path,
        projects.feature.library.googledrive.path,
        projects.feature.library.onedrive.path,
    )
    buildFeatures {
        buildConfig = true
    }

    lint {
        checkAllWarnings = true
        checkDependencies = true
        baseline = file("lint-baseline.xml")
        disable += listOf("InvalidPackage", "NewerVersionAvailable", "GradleDependency")
        htmlReport = true
        htmlOutput = file("$rootDir/build/reports/lint/lint-result.html")
        sarifReport = true
        sarifOutput = file("$rootDir/build/reports/lint/lint-result.sarif")
        textReport = false
        xmlReport = false
    }

    packaging {
        resources.excludes += "com/ramcosta/composedestinations/generated/mermaid/**"
        resources.excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
    }
}

dependencies {
    implementation(projects.framework.ui)
    implementation(projects.framework.notification)
    implementation(projects.framework.designsystem)

    implementation(projects.data.di)
    implementation(projects.domain.usecase)
    implementation(projects.feature.authentication)
    implementation(projects.feature.book)
    implementation(projects.feature.bookshelf)
    implementation(projects.feature.favorite)
    implementation(projects.feature.folder)
    implementation(projects.feature.favorite.add)
    implementation(projects.feature.favorite.create)
    implementation(projects.feature.readlater)
    implementation(projects.feature.search)
    implementation(projects.feature.settings)
    implementation(projects.feature.settings.security)
    implementation(projects.feature.tutorial)
    implementation(projects.feature.library)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.hilt.work)
    implementation(libs.google.android.play.review.ktx)
    implementation(libs.google.android.play.feature.delivery.ktx)
    implementation(libs.androidx.appcompat)

    implementation(libs.google.android.billingclient.billingKtx)

//    debugImplementation(libs.squareup.leakcanary.android)
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
