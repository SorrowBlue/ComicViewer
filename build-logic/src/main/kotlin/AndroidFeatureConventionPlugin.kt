import com.sorrowblue.comicviewer.implementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal class AndroidFeatureConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("comicviewer.android.library")
                apply("comicviewer.android.library.compose")
                apply("comicviewer.android.hilt")
            }

            dependencies {
                implementation(project(":framework:designsystem"))
                implementation(project(":framework:ui"))
            }
        }
    }
}
