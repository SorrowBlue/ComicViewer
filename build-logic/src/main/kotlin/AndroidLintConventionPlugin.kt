import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.Lint
import com.sorrowblue.comicviewer.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.testing.AbstractTestTask
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.gradle.plugin.use.PluginDependency
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal class AndroidLintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            when {
                pluginManager.hasPlugin(libs.plugins.androidApplication) -> {
                    configure<ApplicationExtension> {
                        lint { configure(target) }
                    }
                }

                pluginManager.hasPlugin(libs.plugins.androidLibrary) -> {
                    configure<LibraryExtension> {
                        lint { configure(target) }
                    }
                }

                pluginManager.hasPlugin(libs.plugins.androidMultiplatform) -> {
                    configure<KotlinMultiplatformExtension> {
                        configure<KotlinMultiplatformAndroidLibraryTarget> {
                            lint { configure(target) }
                        }
                    }
                }

                else -> {
                    error("AndroidLintConventionPlugin applied to a project without Android plugin")
                }
            }
            tasks.withType<AbstractTestTask>().configureEach {
                failOnNoDiscoveredTests.set(false)
            }
        }
    }

    private fun PluginManager.hasPlugin(provider: Provider<PluginDependency>): Boolean =
        hasPlugin(provider.get().pluginId)

    private fun Lint.configure(project: Project) {
        val isCI = System.getenv("CI").toBoolean()
        checkAllWarnings = true
        checkDependencies = true
        disable += listOf(
            "InvalidPackage",
            "NewerVersionAvailable",
            "GradleDependency",
            "AppLinksAutoVerify",
        )
        baseline = project.file("lint-baseline.xml")
        htmlReport = !isCI
        htmlOutput =
            if (htmlReport) {
                project.file("${project.rootDir}/build/reports/lint/lint-result.html")
            } else {
                null
            }
        sarifReport = isCI
        sarifOutput =
            if (sarifReport) {
                project.file("${project.rootDir}/build/reports/lint/lint-result.sarif")
            } else {
                null
            }
        textReport = false
        xmlReport = false
    }
}
