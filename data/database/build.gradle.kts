plugins {
    alias(libs.plugins.comicviewer.android.library)
    alias(libs.plugins.comicviewer.android.hilt)
    alias(libs.plugins.androidx.room)
}

android {
    namespace = "com.sorrowblue.comicviewer.data.database"

    sourceSets {
        val test by getting
        test.assets.srcDir(file("$projectDir/schemas"))
    }
}

kotlin {
    jvmToolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(17)
    }
    compilerOptions {
        freeCompilerArgs.add("-opt-in=com.sorrowblue.comicviewer.domain.model.ExperimentalIdValue")
    }
}

dependencies {
    implementation(projects.domain.service)

    implementation(libs.bundles.androidx.room)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.paging.common)

    testImplementation(libs.androidx.room.testing)
}

ksp {
    arg("room.generateKotlin", "true")
}

room {
    schemaDirectory("$projectDir/schemas")
}
