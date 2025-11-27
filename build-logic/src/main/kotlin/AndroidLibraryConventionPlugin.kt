import com.android.build.api.dsl.LibraryExtension
import com.sorrowblue.comicviewer.configureKotlin
import com.sorrowblue.comicviewer.configureLicensee
import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.implementation
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.androidLibrary)

                id(libs.plugins.comicviewer.androidLint)
                id(libs.plugins.comicviewer.detekt)
                id(libs.plugins.comicviewer.dokka)
                id(libs.plugins.licensee)
                id(libs.plugins.aboutlibraries)
            }

            configureKotlin<KotlinAndroidProjectExtension>()

            configure<LibraryExtension> {
                defaultConfig {
                    consumerProguardFiles("consumer-rules.pro")
                }
            }
            configure<KotlinAndroidProjectExtension> {
                compilerOptions {
                    if (project.path.startsWith(":data")) {
                        freeCompilerArgs.add(
                            "-opt-in=com.sorrowblue.comicviewer.domain.model.InternalDataApi"
                        )
                    }
                }
            }
            dependencies {
                implementation(project(":framework:common"))
            }

            configureLicensee()
        }
    }
}
