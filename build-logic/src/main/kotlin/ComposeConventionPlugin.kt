import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.DynamicFeatureExtension
import com.android.build.api.dsl.LibraryExtension
import com.sorrowblue.comicviewer.composeCompiler
import com.sorrowblue.comicviewer.configureAndroidCompose
import com.sorrowblue.comicviewer.debugImplementation
import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.implementation
import com.sorrowblue.comicviewer.kotlin
import com.sorrowblue.comicviewer.ksp
import com.sorrowblue.comicviewer.kspDebug
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.parentName
import com.sorrowblue.comicviewer.plugins
import com.sorrowblue.comicviewer.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension

@Suppress("unused")
internal class ComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.kotlin.compose)
                id(libs.plugins.google.ksp)
            }

            when {
                pluginManager.hasPlugin(libs.plugins.android.application) ->
                    configureAndroidCompose<ApplicationExtension>()

                pluginManager.hasPlugin(libs.plugins.android.library) ->
                    configureAndroidCompose<LibraryExtension>()

                pluginManager.hasPlugin(libs.plugins.android.dynamicFeature) ->
                    configureAndroidCompose<DynamicFeatureExtension>()

                else -> TODO("'${libs.plugins.kotlin.compose.get().pluginId}' cannot be applied to '$name' module.")
            }

            kotlin<KotlinAndroidExtension> {
                compilerOptions {
                    freeCompilerArgs.addAll(
                        "-opt-in=androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi",
                        "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                    )
                }
            }

            composeCompiler {
                val composeCompilerReports: String? by project
                if (composeCompilerReports.toBoolean()) {
                    reportsDestination.set(layout.buildDirectory.dir("compose_compiler"))
                    metricsDestination.set(layout.buildDirectory.dir("compose_compiler"))
                }
            }

            dependencies {
                implementation(libs.compose.destinations.core)
                ksp(libs.compose.destinations.ksp)

                implementation(libs.kotlinx.serialization.json)

                debugImplementation(libs.airbnb.android.showkase)
                implementation(libs.airbnb.android.showkase.annotation)
                kspDebug(libs.airbnb.android.showkase.processor)

                testImplementation(platform(libs.androidx.compose.bom))
                testImplementation(libs.androidx.compose.ui.testManifest)
                testImplementation(libs.androidx.compose.ui.testJunit4)

                implementation(libs.coil3.compose)
                implementation(libs.coil3.networkKtor)

                implementation(platform(libs.koin.bom))
                ksp(libs.koin.kspCompiler)
                implementation(libs.koin.core)
                implementation(libs.koin.annotations)
                implementation(libs.koin.android)
                implementation(libs.koin.compose)
                implementation(libs.koin.composeViewModel)
                implementation(libs.koin.composeViewModelNavigation)
            }

            ksp {
                arg("compose-destinations.codeGenPackageName", "com.sorrowblue.${parentName()}")
                arg("skipPrivatePreviews", "true")
            }
        }
    }
}
