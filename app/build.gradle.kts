@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.konan.properties.propertyString

@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    id("build-logic.android.application")
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
    alias(libs.plugins.dagger.hilt.android)
}

android {
    defaultConfig {
        applicationId = "com.sorrowblue.comicviewer"
        versionCode = 2
        versionName = "1.0"
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
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    packagingOptions {
        resources.excludes.add("META-INF/DEPENDENCIES")
        resources.excludes.add("META-INF/LICENSE")
        resources.excludes.add("META-INF/LICENSE.txt")
        resources.excludes.add("META-INF/license.txt")
        resources.excludes.add("META-INF/NOTICE")
        resources.excludes.add("META-INF/NOTICE.txt")
        resources.excludes.add("META-INF/notice.txt")
        resources.excludes.add("META-INF/ASL2.0")
    }

    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }
}

dependencies {
    implementation(projects.framework.ui)
    implementation(projects.framework.notification)

    implementation(projects.data.di)
    implementation(projects.domain)
    implementation(projects.settings)
    implementation(projects.folder)
    implementation(projects.bookshelf)
    implementation(projects.file.info)
    implementation(projects.favorite)
    implementation(projects.readlater)
    implementation(projects.library)
    implementation(projects.settings.security)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.core.splashscreen)
//    debugImplementation(libs.squareup.leakcanary.android)
    implementation(libs.dagger.hilt.android.core)
    kapt(libs.dagger.hilt.android.compiler)

    androidTestImplementation(libs.androidx.test.ext.junit.ktx)
    androidTestImplementation(libs.androidx.test.espresso.core)
}

kapt {
    correctErrorTypes = true
}
