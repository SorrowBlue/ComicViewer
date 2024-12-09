import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.implementation
import com.sorrowblue.comicviewer.ksp
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

@Suppress("unused")
internal class DaggerHiltConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.google.dagger.hilt)
                id(libs.plugins.google.ksp)
            }

            dependencies {
                implementation(libs.google.dagger.hilt.android)
                ksp(libs.google.dagger.hilt.compiler)
            }
        }
    }
}
