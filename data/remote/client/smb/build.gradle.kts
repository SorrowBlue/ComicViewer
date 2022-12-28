plugins {
    id("com.android.library")
id("org.jetbrains.kotlin.android")
id("build-logic.android.library")

    id("org.jetbrains.kotlin.kapt")
    id("dagger.hilt.android.plugin")
}

android {
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(projects.data.remote.client)

    implementation(libs.squareup.logcat)
    implementation(libs.jcifs.ng)

    implementation(libs.dagger.hilt.android.core)
    kapt(libs.dagger.hilt.android.compiler)

    // Required -- JUnit 4 framework
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.test.ext:junit:1.1.4")
    testImplementation("org.robolectric:robolectric:4.9.1")
    // Optional -- Robolectric environment
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
}

kapt {
    correctErrorTypes = true
}
