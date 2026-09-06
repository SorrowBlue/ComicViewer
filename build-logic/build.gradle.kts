import dev.detekt.gradle.Detekt

plugins {
    `kotlin-dsl`
    alias(libs.plugins.detekt)
    alias(libs.plugins.dependencyAnalysis) apply false
}

group = "com.sorrowblue.comicviewer.buildlogic"

kotlin {
    jvmToolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
    }
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

dependencies {
    implementation("com.autonomousapps:dependency-analysis-gradle-plugin:3.19.1")
    compileOnly(libs.bundles.plugins)
    compileOnly(files(currentLibs.javaClass.superclass.protectionDomain.codeSource.location))
    detektPlugins(libs.bundles.detekt)
    testImplementation(kotlin("test"))
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom(layout.projectDirectory.file("../config/detekt/detekt.yml"))
    basePath.set(projectDir)
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
