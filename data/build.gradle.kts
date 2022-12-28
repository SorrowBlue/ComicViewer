plugins {
    id("com.android.library")
id("org.jetbrains.kotlin.android")
id("build-logic.android.library")

    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
}

dependencies {
    implementation(projects.domain.interactor)
    implementation(projects.data.common)

    implementation(libs.kotlinx.serialization.protobuf)

    implementation(libs.androidx.core.ktx)
    implementation(libs.slf4j.android)
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.squareup.logcat)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.coil)

    implementation(libs.dagger.hilt.android.core)
    kapt(libs.dagger.hilt.android.compiler)

    implementation(libs.bundles.androidx.room)
    ksp(libs.androidx.room.compiler)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

kapt {
    correctErrorTypes = true
}
