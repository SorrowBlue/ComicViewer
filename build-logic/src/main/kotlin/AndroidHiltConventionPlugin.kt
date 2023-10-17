import com.sorrowblue.comicviewer.implementation
import com.sorrowblue.comicviewer.ksp
import com.sorrowblue.comicviewer.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal class AndroidHiltConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.google.devtools.ksp")
            pluginManager.apply("dagger.hilt.android.plugin")

            dependencies {
                implementation(libs.findLibrary("google-dagger-hilt-android").get())
                ksp(libs.findLibrary("google.dagger-compiler").get())
                ksp(libs.findLibrary("google.dagger-hilt-compiler").get())
            }
        }
    }
}
