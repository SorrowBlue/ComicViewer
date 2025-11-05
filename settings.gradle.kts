enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()

        maven {
            name = "Central Portal Snapshots"
            url = uri("https://central.sonatype.com/repository/maven-snapshots/")
            content {
                includeGroup("dev.zacsweers.metro")
            }
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("org.jetbrains.kotlinx.kover.aggregation") version "0.9.2"
    id("com.android.settings") version "8.13.0"
}

android {
    compileSdk = 36
    minSdk = 30
}

kover {
    // -Pkover
    enableCoverage()

    reports {
        // -Pkover.classes.excludes=classes.to.include.*
        excludedClasses.add("androidx.*")
        excludedClasses.add("*generated*")
        excludedClasses.add("*logcat*")

        verify {
            // -Pkover.verify.warn=true
            warningInsteadOfFailure = true
        }
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
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
                includeGroup("dev.zacsweers.metro")
            }
        }
    }
}

rootProject.name = "comicviewer"

include(":aggregation")

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

include(":feature:authentication")
include(":feature:book")
include(":feature:bookshelf")
include(":feature:bookshelf:edit")
include(":feature:bookshelf:info")
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
