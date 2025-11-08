import com.sorrowblue.comicviewer.composeCompiler
import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.kotlin
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.compose)
                id(libs.plugins.google.ksp)
                id(libs.plugins.kotlin.compose)
                id(libs.plugins.kotlin.serialization)
            }

            kotlin<KotlinMultiplatformExtension> {
                sourceSets.commonMain.dependencies {
                    val compose = extensions.getByType<ComposePlugin.Dependencies>()
                    implementation(compose.components.resources)
                    implementation(compose.preview)
                    implementation(libs.androidx.window.core)
                    implementation(libs.compose.multiplatform.material3)
                    implementation(libs.compose.multiplatform.material3AdaptiveNavigationSuite)
                    implementation(libs.compose.multiplatform.material3.adaptive)
                    implementation(libs.compose.multiplatform.material3.adaptiveLayout)
                    implementation(libs.compose.multiplatform.material3.adaptiveNavigation)
                    // Navigation
                    implementation(libs.compose.multiplatform.lifecycleCompose)
                    implementation(libs.multiplatform.lifecycle.viewmodelNavigation3)
                    implementation(libs.kotlinx.serialization.core)

                    implementation(libs.multiplatform.navigation3.ui)
                    implementation(libs.androidx.navigation3.runtime)
                    implementation(libs.multiplatform.navigationevent.compose)
                    implementation(libs.navigation3.resultstate)

                    implementation(libs.androidx.paging.compose)

                    implementation(libs.compose.multiplatform.backhandler)
                    implementation(libs.rin)
                    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.4.0")
                }

                sourceSets.androidMain.dependencies {
                    implementation(libs.androidx.compose.ui.toolingPreview)
                    implementation(libs.androidx.compose.material3.adaptive.navigation3)
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
                val compose = extensions.getByType<ComposePlugin.Dependencies>()
                add("debugImplementation", compose.uiTooling)
            }
        }
    }
}
