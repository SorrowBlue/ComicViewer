import com.sorrowblue.comicviewer.implementation
import com.sorrowblue.comicviewer.ksp
import com.sorrowblue.comicviewer.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal class DaggerHiltConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.google.devtools.ksp")
            pluginManager.apply("dagger.hilt.android.plugin")

            dependencies {
                implementation(libs.google.dagger.hilt.android)
                ksp(libs.google.dagger.compiler)
                ksp(libs.google.dagger.hilt.compiler)
            }
        }
    }
}
