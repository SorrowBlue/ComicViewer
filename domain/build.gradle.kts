plugins {
    id("com.android.library")
id("org.jetbrains.kotlin.android")
id("build-logic.android.library")

    kotlin("plugin.parcelize")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(libs.kotlinx.serialization.core)
    api(libs.androidx.paging.common)
}
