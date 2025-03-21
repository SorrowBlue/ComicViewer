import com.android.build.api.dsl.LibraryExtension
import com.sorrowblue.comicviewer.android
import com.sorrowblue.comicviewer.applyTestImplementation
import com.sorrowblue.comicviewer.configureAndroid
import com.sorrowblue.comicviewer.configureKotlin
import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.implementation
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

@Suppress("unused")
internal class AndroidLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.androidLibrary)
                id(libs.plugins.kotlin.android)
                id(libs.plugins.comicviewer.android.lint)
                id(libs.plugins.comicviewer.detekt)
                id(libs.plugins.comicviewer.dokka)
            }

            configureAndroid<LibraryExtension>()

            configureKotlin<KotlinAndroidProjectExtension>()

            android<LibraryExtension> {
                defaultConfig {
                    consumerProguardFiles("consumer-rules.pro")
                }
            }

            dependencies {
                implementation(project(":framework:common"))

                applyTestImplementation(this@with)
            }
        }
    }
}
