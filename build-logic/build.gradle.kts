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
    compileOnly(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    detektPlugins(libs.nlopez.compose.rules.detekt)
    detektPlugins(libs.arturbosch.detektFormatting)
}

detekt {
    buildUponDefaultConfig = true
    autoCorrect = true
    config.setFrom(layout.projectDirectory.file("../config/detekt/detekt.yml"))
}

gradlePlugin {
    plugins {
        register(libs.plugins.comicviewer.android.application) {
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register(libs.plugins.comicviewer.android.library) {
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register(libs.plugins.comicviewer.android.dynamicFeature) {
            implementationClass = "AndroidDynamicFeatureConventionPlugin"
        }
        register(libs.plugins.comicviewer.android.compose) {
            implementationClass = "ComposeConventionPlugin"
        }
        register(libs.plugins.comicviewer.android.lint) {
            implementationClass = "AndroidLintConventionPlugin"
        }
        register(libs.plugins.comicviewer.android.kotlinMultiplatform) {
            implementationClass = "AndroidKotlinMultiplatformConventionPlugin"
        }
        register(libs.plugins.comicviewer.android.hilt) {
            implementationClass = "DaggerHiltConventionPlugin"
        }
        register(libs.plugins.comicviewer.detekt) {
            implementationClass = "DetektConventionPlugin"
        }
        register(libs.plugins.comicviewer.koin) {
            implementationClass = "KoinConventionPlugin"
        }
        register(libs.plugins.comicviewer.android.feature) {
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register(libs.plugins.comicviewer.android.featureDynamicFeature) {
            implementationClass = "AndroidFeatureDynamicFeatureConventionPlugin"
        }
        register(libs.plugins.comicviewer.dokka) {
            implementationClass = "DokkaConventionPlugin"
        }
    }
}

private fun NamedDomainObjectContainer<PluginDeclaration>.register(
    provider: Provider<PluginDependency>,
    function: PluginDeclaration.() -> Unit,
) = register(provider.get().pluginId) {
    id = name
    function()
}
