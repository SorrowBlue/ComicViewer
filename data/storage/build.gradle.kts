@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("build-logic.android.library")
    id("com.sorrowblue.dagger-hilt")
    alias(libs.plugins.kotlin.plugin.serialization)
}

dependencies {
    implementation(projects.framework)
    implementation(projects.data)
    api(projects.data.reader)
    api(projects.data.common)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coil)
    implementation(libs.androidx.startup.runtime)
    implementation(libs.squareup.logcat)
    implementation(libs.androidx.exifinterface)

    androidTestImplementation(libs.androidx.test.ext.junit.ktx)
    androidTestImplementation(libs.androidx.test.espresso.core)
}
