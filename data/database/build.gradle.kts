@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("build-logic.android.library")
    id("com.sorrowblue.dagger-hilt")
    alias(libs.plugins.google.ksp)
}

dependencies {
    implementation(projects.framework)
    implementation(projects.data)

    implementation(libs.bundles.androidx.room)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.paging.common)

    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.bundles.androidx.instrumented.tests)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
