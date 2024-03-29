import com.sorrowblue.comicviewer.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.DokkaMultiModuleTask

internal class DokkaConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.findPlugin("dokka").get().get().pluginId)
            }
            afterEvaluate {
                rootProject.tasks.withType<DokkaMultiModuleTask>().configureEach {
                    dependsOn(tasks.withType<DokkaMultiModuleTask>())
                }
            }
        }
    }
}
