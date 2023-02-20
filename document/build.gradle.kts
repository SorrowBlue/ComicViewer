plugins {
    id("com.android.dynamic-feature")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.sorrowblue.extention.document"
    compileSdk = 33

    defaultConfig {
        minSdk = 30
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":app"))
    implementation(projects.data.reader)
    implementation(libs.squareup.logcat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.artifex.mupdf.fitz)
}
