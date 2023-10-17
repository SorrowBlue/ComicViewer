import com.android.build.api.dsl.DynamicFeatureExtension
import com.sorrowblue.comicviewer.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

@Suppress("unused")
internal class AndroidDynamicFeatureComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.dynamic-feature")
            val extension = extensions.getByType<DynamicFeatureExtension>()
            configureAndroidCompose(extension)
        }
    }
}
