import com.sorrowblue.comicviewer.diSubprojects

plugins {
    alias(libs.plugins.comicviewer.androidApplication)
    alias(libs.plugins.comicviewer.di)
    alias(libs.plugins.comicviewer.primitive.androidVersionNameCode)
    alias(libs.plugins.androidxBaselineprofile)
}

android {
    namespace = "com.sorrowblue.comicviewer"
    defaultConfig {
        applicationId = "com.sorrowblue.comicviewer"
    }
    androidResources {
        localeFilters += listOf("en", "ja")
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
    diSubprojects.forEach(::implementation)
    baselineProfile(projects.app.androidBenchmark)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.workRuntime)
    implementation(libs.metro.android)
}

baselineProfile {
    dexLayoutOptimization = true
    saveInSrc = false
    automaticGenerationDuringBuild =
        providers.gradleProperty("generateBaselineProfile")
            .map(String::toBoolean)
            .orElse(false)
            .get()
}

metro {
    @Suppress("OPT_IN_USAGE")
    hiddenDependencies {
        checkOnCompile.set(true)
    }
}

aboutLibraries {
    export {
        outputFile.set(isolated.rootProject.projectDirectory.file("feature/settings/info/src/androidMain/composeResources/files/aboutlibraries.json"))
    }
}

tasks.withType<Test>().configureEach {
    failOnNoDiscoveredTests = false
}
