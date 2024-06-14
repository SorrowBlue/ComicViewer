import com.sorrowblue.comicviewer.apply
import com.sorrowblue.comicviewer.implementation
import com.sorrowblue.comicviewer.ksp
import com.sorrowblue.comicviewer.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal class DaggerHiltConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.plugins.google.dagger.hilt)
                apply(libs.plugins.google.ksp)
            }

            dependencies {
                implementation(libs.google.dagger.hilt.android)
                ksp(libs.google.dagger.compiler)
                ksp(libs.google.dagger.hilt.compiler)
            }
        }
    }
}
