import com.android.build.api.dsl.SettingsExtension

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.8.0")
    id("com.android.settings") version "8.5.0"
}

extensions.configure<SettingsExtension> {
    buildToolsVersion = "34.0.0"
    compileSdk = 34
    minSdk = 30
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // com.artifex.mupdf:fitz
        maven { url = uri("https://maven.ghostscript.com/") }
        // com.github.omicronapps:7-Zip-JBinding-4Android
        maven { url = uri("https://jitpack.io/") }

        maven { url = uri("https://androidx.dev/storage/compose-compiler/repository/") }

        maven {
            url =
                uri("https://pkgs.dev.azure.com/MicrosoftDeviceSDK/DuoSDK-Public/_packaging/Duo-SDK-Feed/maven/v1")
        }
    }
}

rootProject.name = "comicviewer"

include(":app")

include(":framework:common")
include(":framework:notification")
include(":framework:designsystem")
include(":framework:ui")

include(":domain:model")
include(":domain:service")
include(":domain:usecase")
include(":domain:reader")

include(":data:coil")
include(":data:database")
include(":data:datastore")
include(":data:reader:zip")
include(":data:reader:document")
include(":data:storage:client")
include(":data:storage:device")
include(":data:storage:smb")
include(":di")

include(":feature:authentication")
include(":feature:book")
include(":feature:bookshelf")
include(":feature:bookshelf:edit")
include(":feature:bookshelf:selection")
include(":feature:favorite")
include(":feature:favorite:add")
include(":feature:favorite:common")
include(":feature:favorite:edit")
include(":feature:file")
include(":feature:folder")
include(":feature:history")
include(":feature:library")
include(":feature:library:box")
include(":feature:library:dropbox")
include(":feature:library:googledrive")
include(":feature:library:onedrive")
include(":feature:readlater")
include(":feature:search")
include(":feature:settings")
include(":feature:settings:common")
include(":feature:settings:display")
include(":feature:settings:folder")
include(":feature:settings:info")
include(":feature:settings:security")
include(":feature:settings:viewer")
include(":feature:tutorial")

includeBuild("build-logic")
