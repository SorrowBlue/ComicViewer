plugins {
    alias(libs.plugins.comicviewer.android.library)
    alias(libs.plugins.comicviewer.android.hilt)
    alias(libs.plugins.androidx.room)
}

android {
    namespace = "com.sorrowblue.comicviewer.data.database"
}

dependencies {
    implementation(projects.domain.service)

    implementation(libs.bundles.androidx.room)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.paging.common)
}

ksp {
    arg("room.generateKotlin", "true")
}

room {
    schemaDirectory("$projectDir/schemas")
}
