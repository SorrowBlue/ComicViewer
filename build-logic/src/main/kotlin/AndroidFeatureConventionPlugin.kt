import com.sorrowblue.comicviewer.apply
import com.sorrowblue.comicviewer.implementation
import com.sorrowblue.comicviewer.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal class AndroidFeatureConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.plugins.comicviewer.android.library)
                apply(libs.plugins.comicviewer.android.compose)
                apply(libs.plugins.comicviewer.android.hilt)
                apply(libs.plugins.comicviewer.android.test)
                apply(libs.plugins.kotlin.plugin.parcelize)
            }

            dependencies {
                implementation(project(":framework:designsystem"))
                implementation(project(":framework:ui"))
                implementation(project(":domain:usecase"))
            }
        }
    }
}
