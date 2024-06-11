import com.sorrowblue.comicviewer.detektPlugins
import com.sorrowblue.comicviewer.libs
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal class DetektConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("io.gitlab.arturbosch.detekt")
            }

            dependencies {
                detektPlugins(libs.nlopez.compose.rules.detekt)
                detektPlugins(libs.arturbosch.detektFormatting)
            }
            extensions.configure<DetektExtension>("detekt") {
                buildUponDefaultConfig = true
                autoCorrect = true
                config.setFrom("${rootProject.projectDir}/config/detekt/detekt.yml")
            }
        }
    }
}
