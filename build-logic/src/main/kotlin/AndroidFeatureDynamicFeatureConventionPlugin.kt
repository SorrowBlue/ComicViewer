import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.implementation
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

@Suppress("unused")
internal class AndroidFeatureDynamicFeatureConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.comicviewer.android.dynamicFeature)
                id(libs.plugins.comicviewer.android.compose)
                id(libs.plugins.kotlin.parcelize)
            }

            dependencies {
                implementation(project(":app"))
                implementation(project(":framework:designsystem"))
                implementation(project(":framework:ui"))
            }
        }
    }
}
