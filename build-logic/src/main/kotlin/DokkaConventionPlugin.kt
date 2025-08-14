import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier

internal class DokkaConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.dokka)
            }

            extensions.configure<DokkaExtension> {
                dokkaSourceSets.configureEach {
                    suppressedFiles.setFrom(layout.buildDirectory.dir("generated"))
                    documentedVisibilities(
                        VisibilityModifier.Public,
                        VisibilityModifier.Internal,
                    )
                }
                dokkaGeneratorIsolation.set(ClassLoaderIsolation())
            }
        }
    }
}
