import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.DynamicFeatureExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.Lint
import com.sorrowblue.comicviewer.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.configure
import org.gradle.plugin.use.PluginDependency

internal class AndroidLintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            when {
                pluginManager.hasPlugin(libs.plugins.android.application) ->
                    configure<ApplicationExtension> {
                        lint { configure(target) }
                    }

                pluginManager.hasPlugin(libs.plugins.android.library) ->
                    configure<LibraryExtension> {
                        lint { configure(target) }
                    }

                pluginManager.hasPlugin(libs.plugins.android.dynamicFeature) ->
                    configure<DynamicFeatureExtension> {
                        lint { configure(target) }
                    }

                else -> {
                    pluginManager.apply("com.android.lint")
                    configure<Lint> {
                        configure(target)
                    }
                }
            }
        }
    }

    private fun PluginManager.hasPlugin(provider: Provider<PluginDependency>): Boolean {
        return hasPlugin(provider.get().pluginId)
    }

    private fun Lint.configure(project: Project) {
        val isCI = System.getenv("CI").toBoolean()
        checkAllWarnings = true
        checkDependencies = true
        disable += listOf(
            "InvalidPackage",
            "NewerVersionAvailable",
            "GradleDependency",
            "AppLinksAutoVerify"
        )
        baseline = project.file("lint-baseline.xml")
        htmlReport = !isCI
        htmlOutput =
            if (htmlReport) project.file("${project.rootDir}/build/reports/lint/lint-result.html") else null
        sarifReport = isCI
        sarifOutput =
            if (sarifReport) project.file("${project.rootDir}/build/reports/lint/lint-result.sarif") else null
        textReport = false
        xmlReport = false
    }
}
