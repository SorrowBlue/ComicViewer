plugins {
    id("com.android.library")
id("org.jetbrains.kotlin.android")
id("build-logic.android.library")

    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.kapt")
    id("dagger.hilt.android.plugin")
}

dependencies {
    implementation(projects.data)
    implementation(projects.data.common)

    implementation(libs.squareup.logcat)
    implementation(libs.bundles.androidx.room)
    ksp(libs.androidx.room.compiler)

    api(libs.androidx.paging.common)

    implementation(libs.dagger.hilt.android.core)
    kapt(libs.dagger.hilt.android.compiler)

}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

kapt {
    correctErrorTypes = true
}
