@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("build-logic.android.library")
    alias(libs.plugins.kotlin.plugin.parcelize)
    alias(libs.plugins.kotlin.plugin.serialization)
}

dependencies {
    implementation(projects.framework)
    implementation(libs.kotlinx.serialization.core)
    api(libs.androidx.datastore.core)
    api(libs.androidx.paging.common)
}
