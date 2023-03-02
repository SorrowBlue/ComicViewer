enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "dagger.hilt.android.plugin") {
                useModule("com.google.dagger:hilt-android-gradle-plugin:${requested.version}")
            }
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.4.0")
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

        maven {
            url = uri("https://pkgs.dev.azure.com/MicrosoftDeviceSDK/DuoSDK-Public/_packaging/Duo-SDK-Feed/maven/v1")
        }
    }
}

rootProject.name = "ComicViewer"

include(":app")

// framework
include(":framework")
include(":framework:notification")
include(":framework:resource")
include(":framework:settings")
include(":framework:ui")

include(":domain")
include(":domain:interactor")

include(":data")
include(":data:common")
include(":data:database")
include(":data:datastore")
include(":data:di")
include(":data:paging")
include(":data:reader")
include(":data:reader:zip")
include(":data:reader:document")
include(":data:remote")
include(":data:remote:device")
include(":data:remote:smb")
include(":data:service")

include(":book")
include(":bookshelf")
include(":favorite")
include(":file")
include(":folder")
include(":folder:display")
include(":library")
include(":library:box")
include(":library:dropbox")
include(":library:googledrive")
include(":library:onedrive")
include(":readlater")

include(":settings")
include(":settings:display")
include(":settings:folder")
include(":settings:security")
include(":settings:viewer")

includeBuild("build-logic")
include(":dynamic")
