import dev.detekt.gradle.Detekt

plugins {
    `kotlin-dsl`
    alias(libs.plugins.detekt)
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
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.metro.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.kotlin.composeCompilerGradlePlugin)
    compileOnly(libs.kover.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
    compileOnly(libs.dokka.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.licensee.gradlePlugin)
    compileOnly(libs.aboutlibraries.gradlePlugin)
    compileOnly(files(currentLibs.javaClass.superclass.protectionDomain.codeSource.location))
    detektPlugins(libs.detekt.compose)
    detektPlugins(libs.detekt.ktlintWrapper)
}

detekt {
    buildUponDefaultConfig = true
    autoCorrect = true
    config.setFrom(layout.projectDirectory.file("../config/detekt/detekt.yml"))
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(false)
        markdown.required.set(false)
        sarif.required.set(true)
        checkstyle.required.set(false)
    }
}

gradlePlugin {
    plugins {
        register(libs.plugins.comicviewer.androidApplication) {
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register(libs.plugins.comicviewer.androidLibrary) {
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register(libs.plugins.comicviewer.di) {
            implementationClass = "DiConventionPlugin"
        }
        register(libs.plugins.comicviewer.multiplatformCompose) {
            implementationClass = "MultiplatformComposeConventionPlugin"
        }
        register(libs.plugins.comicviewer.multiplatformFeature) {
            implementationClass = "MultiplatformFeatureConventionPlugin"
        }
        register(libs.plugins.comicviewer.multiplatformLibrary) {
            implementationClass = "MultiplatformLibraryConventionPlugin"
        }

        register(libs.plugins.comicviewer.androidLint) {
            implementationClass = "AndroidLintConventionPlugin"
        }
        register(libs.plugins.comicviewer.detekt) {
            implementationClass = "DetektConventionPlugin"
        }
        register(libs.plugins.comicviewer.dokka) {
            implementationClass = "DokkaConventionPlugin"
        }
    }
}

// Temporarily set to PushMode only
private val currentLibs get() = libs

private fun NamedDomainObjectContainer<PluginDeclaration>.register(
    provider: Provider<PluginDependency>,
    function: PluginDeclaration.() -> Unit,
) = register(provider.get().pluginId) {
    id = name
    function()
}
