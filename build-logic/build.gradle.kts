import dev.detekt.gradle.Detekt

plugins {
    `kotlin-dsl`
    alias(libs.plugins.detekt)
}

group = "com.sorrowblue.comicviewer.buildlogic"

kotlin {
    jvmToolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = libs.versions.java.map(JavaLanguageVersion::of)
    }
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

dependencies {
    compileOnly(libs.bundles.plugins)
    compileOnly(files(currentLibs.javaClass.superclass.protectionDomain.codeSource.location))
    detektPlugins(libs.bundles.detekt)
    testImplementation(kotlin("test"))
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom(layout.projectDirectory.file("../config/detekt/detekt.yml"))
    basePath.set(layout.projectDirectory)
    autoCorrect = true
    parallel = true
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(false)
        markdown.required.set(false)
        sarif.required.set(true)
        checkstyle.required.set(false)
    }
    exclude {
        it.file.path.run { contains("generated-sources") }
    }
}

// Temporarily set to PushMode only
private val currentLibs get() = libs
