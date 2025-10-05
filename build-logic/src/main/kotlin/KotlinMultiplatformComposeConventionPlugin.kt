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
                    implementation(compose.components.uiToolingPreview)
                    implementation(libs.androidx.window.core)
                    implementation(libs.compose.multiplatform.material3)
                    implementation(libs.compose.multiplatform.material3AdaptiveNavigationSuite)
                    implementation(libs.compose.multiplatform.material3.adaptive)
                    implementation(libs.compose.multiplatform.material3.adaptiveLayout)
                    implementation(libs.compose.multiplatform.material3.adaptiveNavigation)
                    // Navigation
                    implementation(libs.cmpdestinations)
                    implementation(libs.compose.multiplatform.lifecycleCompose)
                    implementation(libs.compose.multiplatform.navigationCompose)
                    implementation(libs.kotlinx.serialization.core)

                    implementation(libs.androidx.paging.compose)

                    implementation(libs.compose.multiplatform.backhandler)
                    implementation(libs.rin)
                }

                sourceSets.androidMain.dependencies {
                    implementation(libs.androidx.compose.ui.toolingPreview)
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
