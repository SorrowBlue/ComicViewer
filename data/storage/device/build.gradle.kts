@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("build-logic.android.library")
    id("com.sorrowblue.dagger-hilt")
}

dependencies {
    implementation(projects.framework)
    implementation(projects.data.storage)

    implementation(libs.squareup.logcat)
    implementation(libs.androidx.documentfile)

    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.bundles.androidx.instrumented.tests)
}
