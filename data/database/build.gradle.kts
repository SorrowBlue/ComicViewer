plugins {
    id("comicviewer.android.library")
    id("comicviewer.android.hilt")
    id("androidx.room")
}

android {
    namespace = "com.sorrowblue.comicviewer.data.database"
}

dependencies {
    implementation(projects.data.infrastructure)

    implementation(libs.bundles.androidx.room)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.paging.common)

    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.bundles.androidx.instrumented.tests)

    modules {
        module("com.google.guava:listenablefuture") {
            replacedBy("com.google.guava:guava", "listenablefuture is part of guava")
        }
    }
}

ksp {
    arg("room.generateKotlin", "true")
}
room {
    schemaDirectory("$projectDir/schemas")
}
