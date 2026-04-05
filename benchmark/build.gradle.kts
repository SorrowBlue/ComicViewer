plugins {
    alias(libs.plugins.androidTest)
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "com.sorrowblue.comicviewer.benchmark"
    compileSdk = 36

    defaultConfig {
        minSdk = 30
        targetSdk = 36
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        create("benchmark") {
            isDebuggable = true
            signingConfig = getByName("debug").signingConfig
            matchingFallbacks += listOf("release")
            buildConfigField("String", "TARGET_PACKAGE", "\"com.sorrowblue.comicviewer\"")
        }
    }

    targetProjectPath = ":app:android"
    experimentalProperties["android.experimental.self-instrumenting"] = true
}

androidComponents {
    beforeVariants(selector().all()) {
        it.enable = it.buildType == "benchmark"
    }
}

dependencies {
    implementation(libs.androidx.testExtJunitKtx)
    implementation(libs.androidx.testRunner)
    implementation(libs.androidx.benchmarkMacroJunit4)
}
