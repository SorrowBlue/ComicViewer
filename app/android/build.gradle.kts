import com.sorrowblue.comicviewer.calculateVersionCode
import com.sorrowblue.comicviewer.gitTagProvider

plugins {
    alias(libs.plugins.comicviewer.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.metro)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.navgraph)
}

android {
    namespace = "com.sorrowblue.comicviewer.app"
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

    buildFeatures {
        compose = true
    }

    packaging {
        resources.excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
    }
    lint {
        checkReleaseBuilds = project.findProperty("androidLintCheckReleaseBuilds") as? Boolean ?: true
        abortOnError = true
    }
}

dependencies {

    rootProject.subprojects.filterNot {
        it.path == project.path || it.path.startsWith(projects.app.path)
    }.forEach {
        val hasSource = it.projectDir.resolve("src").exists()
        if (hasSource) {
            api(it)
        } else {
            logger.lifecycle("Skipping empty or non-source module: ${it.path}")
        }
    }

    implementation(libs.jcifs)
    implementation(projects.app.share)
//    implementation(projects.feature.book)
//    implementation(projects.feature.settings.info)
//    implementation(projects.framework.ui)

    implementation(libs.androidx.activityCompose)
    implementation(libs.androidx.workRuntime)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.coreSplashscreen)
    implementation(libs.androidx.lifecycleCommon)
    implementation(libs.androidx.navigation3UI)
    implementation(libs.compose.ui)
    implementation(libs.compose.preview)
    implementation(libs.metro.android)
    implementation(libs.metro.viewmodelCompose)

    debugImplementation(libs.compose.uiTooling)
}

androidComponents {
    onVariants(selector().all()) { variant ->
        variant.outputs.forEach { output ->
            val versionName = gitTagProvider.orElse("0.0.0").get()
            val versionCode = calculateVersionCode(versionName)
            output.versionName.set(versionName)
            output.versionCode.set(versionCode)
            logger.lifecycle("${variant.name} versionName=$versionName, versionCode=$versionCode")
        }
    }
}
