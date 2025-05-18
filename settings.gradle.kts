enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
    id("com.android.settings") version "8.10.0"
}

android {
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
            name = "gitlab-maven"
            url = uri("https://gitlab.com/api/v4/projects/59936705/packages/maven")
            content {
                includeModule("com.github.shayartzi.sevenzipjbinding", "sevenzipjbinding-all-platforms")
            }
        }

        maven {
            name = "Central Portal Snapshots"
            url = uri("https://central.sonatype.com/repository/maven-snapshots/")
            content {
                includeGroup("com.sorrowblue.cmpdestinations")
            }
        }
    }
}

rootProject.name = "comicviewer"

include(":composeApp")

include(":framework:common")
include(":framework:notification")
include(":framework:designsystem")
include(":framework:test")
include(":framework:ui")

include(":domain:model")
include(":domain:service")
include(":domain:usecase")

include(":data:coil")
include(":data:database")
include(":data:datastore")
include(":data:reader:zip")
include(":data:reader:document")
include(":data:storage:client")
include(":data:storage:smb")
include(":data:storage:")
include(":data:storage:device")
include(":data:di")

include(":feature:authentication")
include(":feature:book")
include(":feature:bookshelf")
include(":feature:bookshelf:edit")
include(":feature:bookshelf:info")
include(":feature:bookshelf:selection")
include(":feature:collection")
include(":feature:collection:add")
include(":feature:collection:editor")
include(":feature:file")
include(":feature:folder")
include(":feature:history")
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
