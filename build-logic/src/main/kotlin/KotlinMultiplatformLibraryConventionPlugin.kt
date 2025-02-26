import com.android.build.api.dsl.LibraryExtension
import com.sorrowblue.comicviewer.configureAndroid
import com.sorrowblue.comicviewer.configureKotlin
import com.sorrowblue.comicviewer.configureKotlinMultiplatform
import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.kotlinMultiplatform)
                id(libs.plugins.android.library)
                id(libs.plugins.comicviewer.android.lint)
                id(libs.plugins.comicviewer.detekt)
            }

            configureKotlin<KotlinMultiplatformExtension>()
            configureKotlinMultiplatform()
            configureAndroid<LibraryExtension>()

            configure<KotlinMultiplatformExtension> {
                sourceSets.commonMain.dependencies {
                    if (project.name != "common" || project.parent?.name != "framework") {
                        implementation(project(":framework:common"))
                    }
                }
            }
        }
    }
}
