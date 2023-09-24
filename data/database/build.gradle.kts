plugins {
    id("comicviewer.android.library")
    id("comicviewer.android.hilt")
}

android {
    namespace = "com.sorrowblue.comicviewer.data.database"
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
