plugins {
    alias(libs.plugins.androidTest)
}

android {
    namespace = "com.sorrowblue.comicviewer.benchmark.test"

    defaultConfig {
        targetSdk = libs.versions.targetSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        create("benchmark") {
            isDebuggable = true
            signingConfig = getByName("debug").signingConfig
            matchingFallbacks += listOf("release")
            buildConfigField("String", "TARGET_PACKAGE", "\"com.sorrowblue.comicviewer.benchmark\"")
        }
    }

    buildFeatures {
        buildConfig = true
    }

    targetProjectPath = projects.app.androidApp.path
    experimentalProperties["android.experimental.self-instrumenting"] = true
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
