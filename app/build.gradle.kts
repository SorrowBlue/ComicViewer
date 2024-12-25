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

    dynamicFeatures += setOf(
        projects.data.reader.document.path,
        projects.feature.library.box.path,
        projects.feature.library.dropbox.path,
        projects.feature.library.googledrive.path,
        projects.feature.library.onedrive.path,
    )

    buildFeatures.buildConfig = true

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
}
