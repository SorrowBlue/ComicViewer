plugins {
    `kotlin-dsl`
}

group = "com.sorrowblue.comicviewer.buildlogic"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

dependencies {
    implementation(libs.android.tools.build.gradle)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.compose.plugin)
    implementation(libs.kotlinx.kover)
    implementation(libs.dokka.gradle.plugin)
    implementation(libs.arturbosch.detektGradlePlugin)
    implementation(libs.ksp.gradlePlugin)
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
        register(libs.plugins.comicviewer.android.lint) {
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
        register(libs.plugins.comicviewer.android.test) {
            implementationClass = "AndroidTestConventionPlugin"
        }
    }
}

fun NamedDomainObjectContainer<PluginDeclaration>.register(
    provider: Provider<PluginDependency>,
    function: PluginDeclaration.() -> Unit,
) =
    register(provider.get().pluginId) {
        id = name
        function()
    }
