import com.sorrowblue.comicviewer.apply
import com.sorrowblue.comicviewer.implementation
import com.sorrowblue.comicviewer.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal class AndroidFeatureDynamicFeatureConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.plugins.comicviewer.android.dynamicFeature)
                apply(libs.plugins.comicviewer.android.compose)
                apply(libs.plugins.kotlin.parcelize)
            }

            dependencies {
                implementation(project(":app"))
                implementation(project(":framework:designsystem"))
                implementation(project(":framework:ui"))
            }
        }
    }
}
