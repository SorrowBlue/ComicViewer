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
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("org.jetbrains.kotlinx.kover.aggregation") version "0.9.9"
    id("com.android.settings") version "9.4.0"
    id("io.github.baole.konture") version "0.8.4"
}

android {
    compileSdk = 37
    minSdk = 31
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
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        maven {
            name = "Central Portal Snapshots"
            url = uri("https://central.sonatype.com/repository/maven-snapshots/")

            content {
                includeGroupByRegex("com.sorrowblue.*")
            }
        }
        mavenLocal()
    }
}

rootProject.name = "comicviewer"

include(":app:androidApp")
include(":app:androidBenchmark")
include(":app:ios")
include(":app:jvmApp")
include(":app:share")

include(":framework:common")
include(":framework:background")
include(":framework:notification")
include(":framework:designsystem")
include(":framework:permission")
include(":framework:startup")
include(":framework:test")
include(":framework:ui")
include(":framework:ui:file")
include(":framework:navkey-processor")

include(":domain:model")
include(":domain:repository")
include(":domain:service")
include(":domain:usecase")

include(":data:coil")
include(":data:database")
include(":data:datastore")
include(":data:reader:zip")
include(":data:reader:document")
include(":data:storage")
include(":data:storage:smb")
include(":data:storage:device")
include(":data:sync")

include(":feature:authentication")
include(":feature:authentication:nav")
include(":feature:book")
include(":feature:book:nav")
include(":feature:bookshelf")
include(":feature:bookshelf:edit")
include(":feature:bookshelf:info")
include(":feature:bookshelf:nav")
include(":feature:collection")
include(":feature:collection:add")
include(":feature:collection:editor")
include(":feature:collection:nav")
include(":feature:folder")
include(":feature:folder:nav")
include(":feature:history")
include(":feature:readlater")
include(":feature:search")
include(":feature:search:nav")
include(":feature:settings")
include(":feature:settings:common")
include(":feature:settings:extension")
include(":feature:settings:display")
include(":feature:settings:folder")
include(":feature:settings:info")
include(":feature:settings:nav")
include(":feature:settings:security")
include(":feature:settings:viewer")
include(":feature:tutorial")
include(":feature:tutorial:nav")
include(":konture-test")

