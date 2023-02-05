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

include(":favorite")

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
include(":data:reader:pdf")
include(":data:reader:zip")
include(":data:remote")
include(":data:remote:device")
include(":data:remote:smb")
include(":data:service")

include(":file")
include(":file:info")
include(":readlater")
include(":server")
include(":server:info")
include(":server:management")
include(":settings")
include(":settings:display")
include(":settings:bookshelf")
include(":settings:security")
include(":settings:viewer")
include(":folder")
include(":folder:display")
include(":book")


includeBuild("build-logic")
