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
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
    id("com.android.settings") version "8.8.0"
}

android {
    buildToolsVersion = "35.0.0"
    compileSdk = 35
    minSdk = 30
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        maven {
            url = uri("https://maven.ghostscript.com/")
            content {
                includeModule("com.artifex.mupdf", "fitz")
            }
        }
        maven {
            url =
                uri("https://pkgs.dev.azure.com/MicrosoftDeviceSDK/DuoSDK-Public/_packaging/Duo-SDK-Feed/maven/v1")
            content {
                includeGroupByRegex("com\\.microsoft.*")
            }
        }
    }
}

rootProject.name = "comicviewer"

include(":composeApp")

include(":framework:common")
include(":framework:notification")
include(":framework:navigation:annotations")
include(":framework:navigation:ksp-compiler")
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
include(":data:storage:smb")
include(":data:di")

include(":feature:authentication")
include(":feature:book")
include(":feature:bookshelf")
include(":feature:bookshelf:edit")
include(":feature:bookshelf:info")
include(":feature:bookshelf:selection")
include(":feature:favorite")
include(":feature:favorite:add")
include(":feature:favorite:common")
include(":feature:favorite:create")
include(":feature:favorite:edit")
include(":feature:file")
include(":feature:folder")
include(":feature:history")
//include(":feature:library")
//include(":feature:library:common")
//include(":feature:library:box")
//include(":feature:library:dropbox")
//include(":feature:library:googledrive")
//include(":feature:library:onedrive")
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
