import com.sorrowblue.comicviewer.implementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal class AndroidFeatureDynamicFeatureConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("comicviewer.android.dynamic-feature")
                apply("comicviewer.android.dynamic-feature.compose")
                apply("org.jetbrains.kotlin.plugin.parcelize")
            }

            dependencies {
                implementation(project(":app"))
                implementation(project(":framework:designsystem"))
                implementation(project(":framework:ui"))
            }
        }
    }
}
