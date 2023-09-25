import com.sorrowblue.comicviewer.implementation
import com.sorrowblue.comicviewer.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal class AndroidFeatureDynamicFeatureConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("comicviewer.android.dynamic-feature")
                apply("comicviewer.android.dynamic-feature.compose")
            }

            dependencies {
                implementation(project(":app"))
                implementation(project(":framework:designsystem"))
                implementation(project(":framework:ui"))

                implementation(libs.findLibrary("androidx-compose-material3-windowSizeClass").get())
                implementation(libs.findLibrary("androidx-hilt-navigationCompose").get())
                implementation(libs.findLibrary("androidx-lifecycle-viewmodelKtx").get())
                implementation(libs.findLibrary("androidx-navigation-compose").get())
                implementation(libs.findLibrary("coil-compose").get())
                implementation(libs.findLibrary("kotlinx-collections-immutable").get())
            }
        }
    }
}
