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
                id(libs.plugins.composeMultiplatform)
                id(libs.plugins.kotlin.compose)
            }

            kotlin<KotlinMultiplatformExtension> {
                sourceSets.commonMain.dependencies {
                    val compose = extensions.getByType<ComposePlugin.Dependencies>()
                    implementation(compose.material3)
                    implementation(compose.components.uiToolingPreview)

                    implementation(libs.coil3.compose)
                    implementation(libs.coil3.networkKtor)
                }

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
                add("debugImplementation", extensions.getByType<ComposePlugin.Dependencies>().uiTooling)
            }
        }
    }
}
