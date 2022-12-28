plugins {
    id("com.android.application")
id("org.jetbrains.kotlin.android")
    id("build-logic.android.application")

    id("org.jetbrains.kotlin.kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
}

android {
    defaultConfig {
        applicationId = "com.sorrowblue.comicviewer"
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
//    packagingOptions  {
//        resources.excludes.add("META-INF/DEPENDENCIES")
//    }
}

dependencies {
    implementation(projects.framework.ui)
    implementation(projects.framework.notification)

    implementation(projects.data.di)
    implementation(projects.domain)
    implementation(projects.settings)
    implementation(projects.bookshelf)
    implementation(projects.server)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.hilt.work)
//    implementation(libs.androidx.biometric)
    implementation(libs.androidx.core.splashscreen)
//    debugImplementation(libs.squareup.leakcanary.android)
    implementation(libs.dagger.hilt.android.core)
    kapt(libs.dagger.hilt.android.compiler)

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}

kapt {
    correctErrorTypes = true
}
