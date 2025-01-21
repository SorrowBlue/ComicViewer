import com.android.build.api.dsl.LibraryExtension
import com.sorrowblue.comicviewer.android
import com.sorrowblue.comicviewer.applyTestImplementation
import com.sorrowblue.comicviewer.configureAndroid
import com.sorrowblue.comicviewer.configureKotlin
import com.sorrowblue.comicviewer.configureKotlinMultiplatform
import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.implementation
import com.sorrowblue.comicviewer.kotlin
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformAndroidLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.kotlinMultiplatform)
                id(libs.plugins.android.library)
                id(libs.plugins.comicviewer.android.lint)
                id(libs.plugins.comicviewer.detekt)
                id(libs.plugins.comicviewer.dokka)
            }

            configureKotlin<KotlinMultiplatformExtension>()
            configureKotlinMultiplatform()
            configureAndroid<LibraryExtension>()

            kotlin<KotlinMultiplatformExtension> {
                sourceSets.commonMain.dependencies {
                    if (project.name != "common" || project.parent?.name != "framework") {
                        implementation(project(":framework:common"))
                    }
                }
            }

            android<LibraryExtension> {
                defaultConfig {
                    consumerProguardFiles("consumer-rules.pro")
                }
            }
            dependencies {
                applyTestImplementation(this@with)
            }
        }
    }
}
