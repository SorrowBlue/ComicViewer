import com.android.build.api.dsl.SettingsExtension

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
//            if (requested.id.id == "dagger.hilt.android.plugin") {
//                useModule("com.google.dagger:hilt-android-gradle-plugin:${requested.version}")
//            }
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.6.0")
    id("com.android.settings") version "8.1.1"
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

        maven {url = uri("https://androidx.dev/storage/compose-compiler/repository/")}

        maven {
            url =
                uri("https://pkgs.dev.azure.com/MicrosoftDeviceSDK/DuoSDK-Public/_packaging/Duo-SDK-Feed/maven/v1")
        }
    }
}

rootProject.name = "ComicViewer"

include(":app")

// framework
include(":framework")
include(":framework:compose")
include(":framework:notification")
include(":framework:resource")
include(":framework:ui")

include(":domain")
include(":domain:common")
include(":domain:interactor")

include(":data")
include(":data:coil")
include(":data:common")
include(":data:database")
include(":data:datastore")
include(":data:di")
include(":data:paging")
include(":data:reader")
include(":data:reader:zip")
include(":data:reader:document")
include(":data:storage")
include(":data:storage:device")
include(":data:storage:smb")
include(":data:service")

include(":feature:book")
include(":feature:bookshelf")
include(":feature:bookshelf:edit")
include(":feature:bookshelf:selection")
include(":feature:favorite")
include(":feature:favorite:add")
include(":feature:favorite:common")
include(":feature:file")
include(":feature:folder")
include(":feature:history")
include(":feature:readlater")
include(":feature:search")
include(":feature:settings")
include(":feature:settings:display")
include(":feature:settings:folder")
include(":feature:settings:security")
include(":feature:settings:viewer")
include(":feature:tutorial")
include(":library")
//include(":library:box")
//include(":library:dropbox")
include(":library:googledrive")
//include(":library:onedrive")

includeBuild("build-logic")
