enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    plugins {
        id("build-logic.android.library") apply false
        id("build-logic.android.application") apply false
id("com.android.application") version "7.4.0" apply false
id("com.android.library") version "7.3.1" apply false
        id("org.jetbrains.kotlin.android") version "1.7.22" apply false
        id("org.jetbrains.kotlin.plugin.parcelize") version "1.7.22" apply false
        id("com.google.devtools.ksp") version "1.7.22-1.0.8" apply false
        id("androidx.navigation.safeargs.kotlin") version "2.6.0-alpha04" apply false
        id("org.jetbrains.kotlin.kapt") version "1.7.22" apply false
        kotlin("plugin.serialization") version "1.7.22" apply false
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "dagger.hilt.android.plugin") {
                useModule("com.google.dagger:hilt-android-gradle-plugin:2.44.2")
            }
        }
    }
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
    }
}

rootProject.name = "ComicViewer"

include(":app")

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
include(":data:remote")
include(":data:remote:client")
include(":data:remote:client:device")
include(":data:remote:client:smb")
include(":data:remote:reader")
include(":data:remote:reader:pdf")
include(":data:remote:reader:zip")
include(":data:service")

include(":server")
include(":server:info")
include(":server:management")
include(":settings")
include(":settings:display")
include(":settings:viewer")
include(":settings:bookshelf")
include(":bookshelf")
include(":book")


includeBuild("build-logic")
