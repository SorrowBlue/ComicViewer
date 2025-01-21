import com.android.build.gradle.internal.ide.kmp.KotlinAndroidSourceSetMarker.Companion.android
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
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.framework.ui)
                implementation(projects.framework.designsystem)
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

                implementation(compose.material3)
                implementation("org.jetbrains.compose.material3.adaptive:adaptive:1.1.0-alpha01")
                implementation("org.jetbrains.compose.material3.adaptive:adaptive-layout:1.1.0-alpha01")
                implementation("org.jetbrains.compose.material3.adaptive:adaptive-navigation:1.1.0-alpha01")
                implementation("org.jetbrains.compose.material3:material3-window-size-class:1.7.3")
                implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha11")
                implementation(compose.material3AdaptiveNavigationSuite)

                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.materialIconsExtended)
                implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.androidx.paging.common)
            }
        }

        androidMain {
            dependencies {
                implementation(projects.framework.ui)
                implementation(projects.framework.notification)
                implementation(projects.framework.designsystem)
                implementation(projects.data.di)
                implementation(projects.domain.usecase)
                implementation(projects.feature.settings)
                implementation(projects.feature.settings.security)
                implementation(projects.feature.library)
                implementation(compose.preview)
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

    buildFeatures.buildConfig = true

    packaging {
        resources.excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
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
        mainClass = "com.sorrowblue.comicviewer.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.sorrowblue.comicviewer"
            packageVersion = versionNameGit
        }
    }
}
