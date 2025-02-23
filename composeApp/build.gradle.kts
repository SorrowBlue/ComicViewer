import com.sorrowblue.comicviewer.ComicBuildType
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.application)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.compose)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.aboutlibraries)
}

kotlin {
    val xcFramework = XCFramework()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            binaryOption("bundleId", "com.sorrowblue.comicviewer.app")
            xcFramework.add(this)
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.framework.designsystem)
                implementation(projects.framework.ui)
                implementation("com.sorrowblue:annotations:1.0")
                implementation(projects.data.di)
                implementation(projects.domain.usecase)
                implementation(projects.feature.authentication)
                implementation(projects.feature.bookshelf)
                implementation(projects.feature.bookshelf.info)
                implementation(projects.feature.book)
                implementation(projects.feature.readlater)
                implementation(projects.feature.favorite)
                implementation(projects.feature.favorite.add)
                implementation(projects.feature.favorite.create)
                implementation(projects.feature.search)
                implementation(projects.feature.tutorial)
                implementation(projects.feature.folder)
                implementation(projects.feature.settings)
                implementation(projects.feature.history)

                implementation(compose.material3AdaptiveNavigationSuite)
            }
        }

        androidMain {
            dependencies {
                implementation(projects.framework.notification)

                implementation(libs.androidx.activity)
                implementation(libs.androidx.biometric)
                implementation(libs.androidx.browser)
                implementation(libs.androidx.core.splashscreen)
                implementation(libs.google.android.play.review.ktx)
                implementation(libs.google.android.play.feature.delivery.ktx)
                implementation(libs.androidx.appcompat)
                implementation(libs.koin.androidxCompose)
                implementation(libs.koin.androidxStartup)
                implementation(libs.koin.androidxWorkmanager)
                implementation(libs.google.android.billingclient.billingKtx)
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}
val versionNameGit = "1.0.0"

android {
    namespace = "com.sorrowblue.comicviewer.app"
    defaultConfig {
        applicationId = "com.sorrowblue.comicviewer"
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = versionNameGit
        logger.lifecycle("versionName=$versionName")
    }
    androidResources {
        generateLocaleConfig = true
    }

    buildTypes {
        // TODO(Remove suffix .kmp)
        val suffix = ".kmp"
        release {
            applicationIdSuffix = ComicBuildType.RELEASE.applicationIdSuffix + suffix
            isMinifyEnabled = ComicBuildType.RELEASE.isMinifyEnabled
            isShrinkResources = ComicBuildType.RELEASE.isShrinkResources
            signingConfig = signingConfigs.findByName(name)
        }
        getByName(ComicBuildType.PRERELEASE.display) {
            initWith(getByName(ComicBuildType.RELEASE.display))
            applicationIdSuffix = ComicBuildType.PRERELEASE.applicationIdSuffix + suffix
            isMinifyEnabled = ComicBuildType.PRERELEASE.isMinifyEnabled
            isShrinkResources = ComicBuildType.PRERELEASE.isShrinkResources
            signingConfig = signingConfigs.findByName(name)
        }
        getByName(ComicBuildType.INTERNAL.display) {
            initWith(getByName(ComicBuildType.RELEASE.display))
            applicationIdSuffix = ComicBuildType.INTERNAL.applicationIdSuffix + suffix
            isMinifyEnabled = ComicBuildType.INTERNAL.isMinifyEnabled
            isShrinkResources = ComicBuildType.INTERNAL.isShrinkResources
            signingConfig = signingConfigs.findByName(name)
        }
        debug {
            applicationIdSuffix = ComicBuildType.DEBUG.applicationIdSuffix + suffix
            isMinifyEnabled = ComicBuildType.DEBUG.isMinifyEnabled
            isShrinkResources = ComicBuildType.DEBUG.isShrinkResources
            signingConfig = signingConfigs.findByName(name)
        }
    }

    // TODO(dynamicFeatures)

    dynamicFeatures += setOf(projects.data.reader.document.path)

    buildFeatures.buildConfig = true

    packaging {
        resources.excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
    }

    lint {
        abortOnError = true
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    add("kspCommonMainMetadata", projects.framework.navigation.kspCompiler)
    add("kspAndroid", projects.framework.navigation.kspCompiler)
    add("kspIosX64", projects.framework.navigation.kspCompiler)
    add("kspIosArm64", projects.framework.navigation.kspCompiler)
    add("kspIosSimulatorArm64", projects.framework.navigation.kspCompiler)
    add("kspDesktop", projects.framework.navigation.kspCompiler)
}

compose.desktop {
    application {
        mainClass = "com.sorrowblue.comicviewer.app.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.sorrowblue.comicviewer.app"
            packageVersion = versionNameGit
        }
        jvmArgs("-Dsun.stdout.encoding=UTF-8", "-Dsun.stderr.encoding=UTF-8")
    }
}
