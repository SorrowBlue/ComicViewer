import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.kotlin
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.plugins
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformDiConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.kotlin.multiplatform)
                id(libs.plugins.google.ksp)
                id(libs.plugins.metro)
            }

            kotlin<KotlinMultiplatformExtension> {
            }

            metro {
                contributesAsInject.set(true)
            }

            dependencies {
            }
        }
    }
}

private fun Project.metro(configure: Action<dev.zacsweers.metro.gradle.MetroPluginExtension>): Unit =
    (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("metro", configure)
