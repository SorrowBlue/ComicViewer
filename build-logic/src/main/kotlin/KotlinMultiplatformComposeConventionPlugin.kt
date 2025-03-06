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
                id(libs.plugins.google.ksp)
                id(libs.plugins.kotlin.compose)
                id(libs.plugins.kotlin.serialization)
            }

            kotlin<KotlinMultiplatformExtension> {
                sourceSets.commonMain.dependencies {
                    val compose = extensions.getByType<ComposePlugin.Dependencies>()
                    implementation(compose.components.resources)
                    implementation(compose.components.uiToolingPreview)

                    // Navigation
                    implementation(libs.cmpdestinations)
                    implementation(libs.compose.multiplatform.navigationCompose)
                    implementation(libs.kotlinx.serialization.core)
                }

                sourceSets.androidMain.dependencies {
                    implementation(libs.androidx.compose.ui.toolingPreview)
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
                val compose = extensions.getByType<ComposePlugin.Dependencies>()
                add("debugImplementation", compose.uiTooling)

                add("kspCommonMainMetadata", libs.cmpdestinations.ksp)
                add("kspAndroid", libs.cmpdestinations.ksp)
                add("kspAndroidTest", libs.cmpdestinations.ksp)
                add("kspIosX64", libs.cmpdestinations.ksp)
                add("kspIosX64Test", libs.cmpdestinations.ksp)
                add("kspIosArm64", libs.cmpdestinations.ksp)
                add("kspIosArm64Test", libs.cmpdestinations.ksp)
                add("kspIosSimulatorArm64", libs.cmpdestinations.ksp)
                add("kspIosSimulatorArm64Test", libs.cmpdestinations.ksp)
                add("kspDesktop", libs.cmpdestinations.ksp)
                add("kspDesktopTest", libs.cmpdestinations.ksp)
            }
        }
    }
}
