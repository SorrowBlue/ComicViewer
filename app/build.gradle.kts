import com.sorrowblue.buildlogic.kotlinOptions

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("build-logic.android.application")
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
    alias(libs.plugins.dagger.hilt.android)
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
    kotlinOptions {
        println("jvmTarget = ${jvmTarget}")
    }
    dataBinding.enable = true
    viewBinding.enable = true
}

dependencies {
    implementation(projects.framework)
    implementation(projects.framework.ui)
    implementation(projects.framework.notification)

    implementation(projects.data.di)
    implementation(projects.domain)
    implementation(projects.settings)
    implementation(projects.folder)
    implementation(projects.bookshelf)
    implementation(projects.file.info)
    implementation(projects.favorite)
    implementation(projects.readlater)
    implementation(projects.settings.security)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.core.splashscreen)
//    debugImplementation(libs.squareup.leakcanary.android)
    implementation(libs.dagger.hilt.android.core)
    kapt(libs.dagger.hilt.android.compiler)

    androidTestImplementation(libs.androidx.test.ext.junit.ktx)
    androidTestImplementation(libs.androidx.test.espresso.core)
}

kapt {
    correctErrorTypes = true
}
