import com.sorrowblue.comicviewer.libs
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal class AndroidLintConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("io.gitlab.arturbosch.detekt")
            }

            dependencies {
                add("detektPlugins", libs.findLibrary("nlopez.compose.rules.detekt").get())
                add("detektPlugins", libs.findLibrary("arturbosch.detektFormatting").get())
            }
            extensions.configure<DetektExtension>("detekt") {
                buildUponDefaultConfig = true
                autoCorrect = true
                config.setFrom("${rootProject.projectDir}/config/detekt/detekt.yml")
            }
        }
    }
}
