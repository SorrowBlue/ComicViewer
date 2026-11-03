import com.sorrowblue.comicviewer.configureManagedDevices

plugins {
    alias(libs.plugins.comicviewer.primitive.detekt)
    alias(libs.plugins.androidTest)
    alias(libs.plugins.androidxBaselineprofile)
}

android {
    namespace = "com.sorrowblue.comicviewer.benchmark.test"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "TARGET_PACKAGE", "\"com.sorrowblue.comicviewer\"")
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

    configureManagedDevices()
}

baselineProfile {
    managedDevices += "pixel7pro35"
    useConnectedDevices = false
}

dependencies {
    implementation(libs.androidx.benchmarkMacroJunit4)
    implementation(libs.androidx.testExtJunit)
    implementation(libs.junit)
}
