import com.android.build.api.dsl.DynamicFeatureExtension
import com.google.devtools.ksp.gradle.KspExtension
import com.sorrowblue.comicviewer.configureAndroidCompose
import com.sorrowblue.comicviewer.implementation
import com.sorrowblue.comicviewer.ksp
import com.sorrowblue.comicviewer.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

@Suppress("unused")
internal class AndroidDynamicFeatureComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.dynamic-feature")
                apply("com.google.devtools.ksp")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            val extension = extensions.getByType<DynamicFeatureExtension>()
            configureAndroidCompose(extension)

            dependencies {
                implementation(libs.findLibrary("compose-destinations-core").get())
                ksp(libs.findLibrary("compose-destinations-ksp").get())
            }

            configure<KspExtension> {
                arg("compose-destinations.mode", "destinations")
            }
        }
    }
}
