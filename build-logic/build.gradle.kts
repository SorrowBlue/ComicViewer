plugins {
    `kotlin-dsl`
}

group = "com.sorrowblue.comicviewer.buildlogic"

kotlin {
    jvmToolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(17)
    }
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.kotlin.compose.gradlePlugin)
    implementation(libs.kotlinx.kover.gradlePlugin)
    implementation(libs.google.ksp.gradlePlugin)
    implementation(libs.detekt.gradlePlugin)
    implementation(libs.dokka.gradlePlugin)
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
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
) =
    register(provider.get().pluginId) {
        id = name
        function()
    }
