@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("build-logic.android.library")
    id("com.sorrowblue.dagger-hilt")
    alias(libs.plugins.kotlin.plugin.serialization)
}

dependencies {
    implementation(projects.framework)
    implementation(projects.data)
    api(projects.data.common)
    api(projects.data.reader)

    androidTestImplementation(libs.androidx.test.ext.junit.ktx)
    androidTestImplementation(libs.androidx.test.espresso.core)
}
