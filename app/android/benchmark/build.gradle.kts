import com.android.build.api.dsl.ApplicationExtension

plugins {
    alias(libs.plugins.androidTest)
}

android {
    namespace = "com.sorrowblue.comicviewer.app.benchmark"

    defaultConfig {
        targetSdk = libs.versions.targetSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        create("benchmark") {
            isDebuggable = true
            signingConfig = getByName("debug").signingConfig
            matchingFallbacks += listOf("release")
            buildConfigField("String", "TARGET_PACKAGE", "\"${getBenchmarkApplicationId()}\"")
        }
    }

    buildFeatures {
        buildConfig = true
    }

    targetProjectPath = projects.app.android.path
    experimentalProperties["android.experimental.self-instrumenting"] = true
}

fun getBenchmarkApplicationId(): String {
    val appProject = project(projects.app.android.path)
    val appExtension = appProject.extensions.getByType<ApplicationExtension>()
    val baseId = appExtension.defaultConfig.applicationId ?: ""
    val debugSuffix = appExtension.buildTypes.getByName("benchmark").applicationIdSuffix ?: ""
    return "$baseId$debugSuffix"
}

dependencies {
    implementation(libs.androidx.benchmarkMacroJunit4)
    implementation(libs.androidx.test.espressoCore)
    implementation(libs.androidx.testExtJunitKtx)
    implementation(libs.androidx.testUiautomator)
}

androidComponents {
    beforeVariants(selector().all()) {
        it.enable = it.buildType == "benchmark"
    }
}
