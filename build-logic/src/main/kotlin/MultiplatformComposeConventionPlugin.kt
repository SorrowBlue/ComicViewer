import com.sorrowblue.comicviewer.composeCompiler
import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.plugin.use.PluginDependency
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class MultiplatformComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.composeMultiplatform)
                id(libs.plugins.composeCompiler)
                id(libs.plugins.kotlinSerialization)
            }

            configure<KotlinMultiplatformExtension> {
                sourceSets {
                    commonMain.dependencies {
                        implementation(libs.compose.components.resources)
                        implementation(libs.compose.preview)
                        implementation(libs.compose.uiBackhandler)
                        implementation(libs.kotlinx.collectionsImmutable)
                        implementation(libs.rin)

                        // Adaptive
                        implementation(libs.androidx.windowCore)
                        implementation(libs.compose.material3)
                        implementation(libs.compose.material3Adaptive)
                        implementation(libs.compose.material3AdaptiveLayout)
                        implementation(libs.compose.material3AdaptiveNavigation3)
                        implementation(libs.compose.material3AdaptiveNavigationSuite)

                        // Navigation
                        implementation(libs.androidx.lifecycleCompose)
                        implementation(libs.androidx.lifecycleViewmodelNavigation3)
                        implementation(libs.androidx.navigation3UI)
                        implementation(libs.androidx.navigation3Runtime)
                        implementation(libs.androidx.navigationeventCompose)
                        implementation(libs.kotlinx.serializationCore)
                        implementation(libs.navigation3.resultstate)

                        // Paging
                        implementation(libs.androidx.pagingCompose)
                    }
                }

                compilerOptions {
                    freeCompilerArgs.addAll(
                        "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                        "-opt-in=androidx.compose.material3.ExperimentalMaterial3ExpressiveApi",
                        "-opt-in=androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi",
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
                if(pluginManager.hasPlugin(libs.plugins.androidMultiplatform)) {
                    add("androidRuntimeClasspath", libs.compose.uiTooling)
                }
            }
        }
    }
}
private fun PluginManager.hasPlugin(provider: Provider<PluginDependency>): Boolean =
    hasPlugin(provider.get().pluginId)

