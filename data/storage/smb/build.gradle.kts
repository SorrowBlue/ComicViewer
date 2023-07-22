@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

plugins {
    id("build-logic.android.library")
    id("com.sorrowblue.dagger-hilt")
}

android {
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(projects.framework)
    implementation(projects.data.storage)

    implementation(libs.squareup.logcat)
    implementation(libs.jcifs.ng)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Required -- JUnit 4 framework
    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.core.ktx)
    testImplementation(libs.androidx.test.ext.junit.ktx)
    // Optional -- Robolectric environment
    testImplementation(libs.kotlinx.coroutines.test)


    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.bundles.androidx.instrumented.tests)
}
