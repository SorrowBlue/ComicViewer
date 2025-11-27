import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.sorrowblue.comicviewer.configureKotlin
import com.sorrowblue.comicviewer.configureKotlinMultiplatformSourceSets
import com.sorrowblue.comicviewer.configureLicensee
import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class MultiplatformLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.kotlinMultiplatform)
                id(libs.plugins.androidMultiplatform)

                id(libs.plugins.comicviewer.androidLint)
                id(libs.plugins.comicviewer.detekt)
                id(libs.plugins.comicviewer.dokka)
                id(libs.plugins.licensee)
                id(libs.plugins.aboutlibraries)
            }

            configureKotlinMultiplatformSourceSets()

            configureKotlin<KotlinMultiplatformExtension>()

            configure<KotlinMultiplatformExtension> {
                configure<KotlinMultiplatformAndroidLibraryTarget> {
                    optimization {
                        consumerKeepRules.file("consumer-rules.pro")
                    }
                }
                compilerOptions {
                    if (project.path.startsWith(":data")) {
                        freeCompilerArgs.add(
                            "-opt-in=com.sorrowblue.comicviewer.domain.model.InternalDataApi"
                        )
                    }
                }
                sourceSets {
                    commonMain.dependencies {
                        if (project.path != ":framework:common") {
                            implementation(project(":framework:common"))
                        }
                    }
                }
            }

            configureLicensee()
        }
    }
}
