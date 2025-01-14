plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.di)
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
    compilerOptions {
        freeCompilerArgs.add("-opt-in=com.sorrowblue.comicviewer.domain.model.ExperimentalIdValue")
    }

    sourceSets.commonMain.dependencies {
        implementation(projects.domain.service)
        implementation(libs.androidx.room.runtime)
        implementation(libs.androidx.room.paging)
        implementation(libs.androidx.sqlite.bundled)
        implementation(libs.androidx.paging.common)
    }
}

dependencies {
    add("kspIosX64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspDesktop", libs.androidx.room.compiler)
    add("kspAndroid", libs.androidx.room.compiler)
//    testImplementation(libs.androidx.room.testing)
}

ksp {
    arg("room.generateKotlin", "true")
}

room {
    schemaDirectory("$projectDir/schemas")
}
