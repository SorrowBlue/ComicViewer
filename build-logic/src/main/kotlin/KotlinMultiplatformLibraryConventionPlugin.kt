import com.android.build.api.dsl.LibraryExtension
import com.sorrowblue.comicviewer.configureAndroid
import com.sorrowblue.comicviewer.configureKotlin
import com.sorrowblue.comicviewer.configureKotlinMultiplatform
import com.sorrowblue.comicviewer.configureLicensee
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
                id(libs.plugins.kotlin.multiplatform)
                id(libs.plugins.android.library)
                id(libs.plugins.comicviewer.android.lint)
                id(libs.plugins.comicviewer.detekt)
                id(libs.plugins.comicviewer.dokka)
                id(libs.plugins.licensee)
            }

            configureKotlin<KotlinMultiplatformExtension>()
            configureKotlinMultiplatform()
            configure<LibraryExtension> { configureAndroid(this) }

            configure<KotlinMultiplatformExtension> {
                compilerOptions {
                    if (project.path.startsWith(":data")) {
                        freeCompilerArgs.add(
                            "-opt-in=com.sorrowblue.comicviewer.domain.model.InternalDataApi",
                        )
                    }
                }
                sourceSets.commonMain.dependencies {
                    if (project.name != "common" || project.parent?.name != "framework") {
                        implementation(project(":framework:common"))
                    }
                }
            }

            configureLicensee()
        }
    }
}
