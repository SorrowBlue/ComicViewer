plugins {
    alias(libs.plugins.comicviewer.androidApplication)
    alias(libs.plugins.metro)
    id("comicviewer.primitive.android-version-name-code")
}

android {
    namespace = "com.sorrowblue.comicviewer"
    defaultConfig {
        applicationId = "com.sorrowblue.comicviewer"
        targetSdk = libs.versions.targetSdk.get().toInt()
    }
    androidResources {
        @Suppress("UnstableApiUsage")
        generateLocaleConfig = true
    }
    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            signingConfig = signingConfigs.findByName("debug")
        }
        val release = getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.findByName("release")
        }
        create("prerelease") {
            initWith(release)
            applicationIdSuffix = ".prerelease"
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.findByName("release")
            matchingFallbacks += listOf("release")
        }
        create("internal") {
            initWith(release)
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.findByName("release")
            matchingFallbacks += listOf("release")
        }
        create("benchmark") {
            initWith(release)
            applicationIdSuffix = ".benchmark"
            signingConfig = signingConfigs.findByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
        }
    }
    packaging {
        resources.excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
    }
    lint {
        checkReleaseBuilds =
            project.findProperty("androidLintCheckReleaseBuilds") as? Boolean ?: true
        abortOnError = true
    }
}

dependencies {
    implementation(projects.app.share)
    implementation(libs.jcifs)
    implementation(libs.androidx.workRuntime)
    implementation(libs.metro.android)
    implementation(libs.metro.viewmodelCompose)
}

aboutLibraries {
    export {
        outputFile.set(rootProject.layout.projectDirectory.file("feature/settings/info/src/androidMain/composeResources/files/aboutlibraries.json"))
    }
}
