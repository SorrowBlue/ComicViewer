import com.sorrowblue.comicviewer.ComicBuildType
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.application)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.compose)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            binaryOption("bundleId", "com.sorrowblue.comicviewer.app")
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.framework.designsystem)
            implementation(projects.framework.ui)
            implementation(projects.data.di)
            implementation(projects.domain.usecase)
            implementation(projects.feature.authentication)
            implementation(projects.feature.bookshelf)
            implementation(projects.feature.bookshelf.info)
            implementation(projects.feature.book)
            implementation(projects.feature.readlater)
            implementation(projects.feature.collection)
            implementation(projects.feature.search)
            implementation(projects.feature.tutorial)
            implementation(projects.feature.folder)
            implementation(projects.feature.settings)
            implementation(projects.feature.settings.info)
            implementation(projects.feature.history)

            // Material3
            implementation(compose.material3)
            implementation(compose.material3AdaptiveNavigationSuite)
            implementation(libs.compose.multiplatform.material3.adaptive)
            // Di
            implementation(libs.koin.composeViewModel)

            implementation(libs.kotlinx.serialization.json)
        }

        androidMain.dependencies {
            implementation(projects.framework.notification)

            implementation(libs.androidx.core.splashscreen)
            implementation(libs.koin.androidxCompose)
            implementation(libs.koin.androidxStartup)
            implementation(libs.koin.androidxWorkmanager)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.google.android.play.feature.delivery.ktx)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

compose.resources {
    customDirectory(
        sourceSetName = "androidMain",
        directoryProvider = layout.buildDirectory.dir("generated/aboutLibrariesExt/androidMain/composeResources")
    )
    customDirectory(
        sourceSetName = "desktopMain",
        directoryProvider = layout.buildDirectory.dir("generated/aboutLibrariesExt/desktopMain/composeResources")
    )
    customDirectory(
        sourceSetName = "iosMain",
        directoryProvider = layout.buildDirectory.dir("generated/aboutLibrariesExt/iosMain/composeResources")
    )
}

android {
    namespace = "com.sorrowblue.comicviewer.app"
    defaultConfig {
        applicationId = "com.sorrowblue.comicviewer"
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = version.toString()
    }
    androidResources {
        generateLocaleConfig = true
    }

    buildTypes {
        release {
            applicationIdSuffix = ComicBuildType.RELEASE.applicationIdSuffix
            isMinifyEnabled = ComicBuildType.RELEASE.isMinifyEnabled
            isShrinkResources = ComicBuildType.RELEASE.isShrinkResources
            signingConfig = signingConfigs.findByName(name)
        }
        getByName(ComicBuildType.PRERELEASE.display) {
            initWith(getByName(ComicBuildType.RELEASE.display))
            applicationIdSuffix = ComicBuildType.PRERELEASE.applicationIdSuffix
            isMinifyEnabled = ComicBuildType.PRERELEASE.isMinifyEnabled
            isShrinkResources = ComicBuildType.PRERELEASE.isShrinkResources
            signingConfig = signingConfigs.findByName(name)
        }
        getByName(ComicBuildType.INTERNAL.display) {
            initWith(getByName(ComicBuildType.RELEASE.display))
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

    dynamicFeatures += setOf(projects.data.reader.document.path)

    buildFeatures.buildConfig = true

    packaging {
        resources.excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
    }

    this.sourceSets {
        this.getByName("main").res.srcDirs("src/commonMain/composeResources")
    }

    lint {
        abortOnError = true
    }
}

compose.desktop {
    application {
        mainClass = "com.sorrowblue.comicviewer.app.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.sorrowblue.comicviewer.app"
            packageVersion = "1.0.0"
        }
        jvmArgs("-Dsun.stdout.encoding=UTF-8", "-Dsun.stderr.encoding=UTF-8")
    }
}
