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
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.kotlin.compose.gradlePlugin)
    compileOnly(libs.kotlinx.kover.gradlePlugin)
    compileOnly(libs.google.ksp.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
    compileOnly(libs.dokka.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(files(currentLibs.javaClass.superclass.protectionDomain.codeSource.location))
    detektPlugins(libs.nlopez.compose.rules.detekt)
    detektPlugins(libs.arturbosch.detektFormatting)
}

detekt {
    buildUponDefaultConfig = true
    autoCorrect = true
    config.setFrom(layout.projectDirectory.file("../config/detekt/detekt.yml"))
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        html.required.set(false)
        md.required.set(false)
        sarif.required.set(true)
        txt.required.set(false)
        xml.required.set(false)
    }
}

gradlePlugin {
    plugins {
        register(libs.plugins.comicviewer.android.library) {
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register(libs.plugins.comicviewer.android.lint) {
            implementationClass = "AndroidLintConventionPlugin"
        }
        register(libs.plugins.comicviewer.detekt) {
            implementationClass = "DetektConventionPlugin"
        }
        register(libs.plugins.comicviewer.dokka) {
            implementationClass = "DokkaConventionPlugin"
        }
        register(libs.plugins.comicviewer.kotlinMultiplatform.application) {
            implementationClass = "KotlinMultiplatformApplicationConventionPlugin"
        }
        register(libs.plugins.comicviewer.kotlinMultiplatform.library) {
            implementationClass = "KotlinMultiplatformLibraryConventionPlugin"
        }
        register(libs.plugins.comicviewer.kotlinMultiplatform.compose) {
            implementationClass = "KotlinMultiplatformComposeConventionPlugin"
        }
        register(libs.plugins.comicviewer.kotlinMultiplatform.koin) {
            implementationClass = "KotlinMultiplatformKoinConventionPlugin"
        }
        register(libs.plugins.comicviewer.kotlinMultiplatform.dynamicfeature) {
            implementationClass = "KotlinMultiplatformAndroidDynamicFeatureConventionPlugin"
        }
        register(libs.plugins.comicviewer.kotlinMultiplatform.feature) {
            implementationClass = "KotlinMultiplatformFeatureConventionPlugin"
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
