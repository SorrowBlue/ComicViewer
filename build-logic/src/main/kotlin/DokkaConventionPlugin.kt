import com.sorrowblue.comicviewer.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

internal class DokkaConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.findPlugin("dokka").get().get().pluginId)
            }
        }
    }
}
