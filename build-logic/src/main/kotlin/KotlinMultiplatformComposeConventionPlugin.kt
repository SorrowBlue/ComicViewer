import com.sorrowblue.comicviewer.composeCompiler
import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.kotlin
import com.sorrowblue.comicviewer.ksp
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.parentName
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
                id(libs.plugins.kotlin.serialization)
                id(libs.plugins.google.ksp)
            }

            kotlin<KotlinMultiplatformExtension> {
                sourceSets.commonMain.dependencies {
                    val compose = extensions.getByType<ComposePlugin.Dependencies>()
                    implementation(compose.material3)
                    implementation(compose.components.resources)
                    implementation(compose.components.uiToolingPreview)

                    implementation(libs.coil3.compose)
                    implementation(libs.coil3.networkKtor)
                    implementation(libs.kotlinx.serialization.core)

                    implementation(project.dependencies.platform(libs.koin.bom))
                    implementation(libs.koin.annotations)
                    implementation(libs.koin.compose)
                    implementation(libs.koin.composeViewModel)
                    implementation(libs.koin.composeViewModelNavigation)
                }

                sourceSets.androidMain.dependencies {
                    // TODO Remove
                    implementation(libs.compose.destinations.core)
                    implementation(libs.kotlinx.serialization.json)
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
                add(
                    "debugImplementation",
                    extensions.getByType<ComposePlugin.Dependencies>().uiTooling
                )
                add("kspAndroid", libs.compose.destinations.ksp)
                add("kspCommonMainMetadata", libs.koin.kspCompiler)
                add("kspAndroid", libs.koin.kspCompiler)
                add("kspIosX64", libs.koin.kspCompiler)
                add("kspIosArm64", libs.koin.kspCompiler)
                add("kspIosSimulatorArm64", libs.koin.kspCompiler)
                add("kspDesktop", libs.koin.kspCompiler)
            }
            // TODO Remove
            ksp {
                arg("compose-destinations.codeGenPackageName", "com.sorrowblue.${parentName()}")
                arg("KOIN_USE_COMPOSE_VIEWMODEL", "true")
                arg("KOIN_CONFIG_CHECK", "false")
            }
        }
    }
}
