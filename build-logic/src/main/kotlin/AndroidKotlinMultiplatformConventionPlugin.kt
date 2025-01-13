import com.android.build.api.dsl.LibraryExtension
import com.sorrowblue.comicviewer.configureAndroid
import com.sorrowblue.comicviewer.configureKotlin
import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.kotlin
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
internal class AndroidKotlinMultiplatformConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.kotlinMultiplatform)
                id(libs.plugins.android.library)
                id(libs.plugins.comicviewer.android.lint)
                id(libs.plugins.comicviewer.detekt)
                id(libs.plugins.comicviewer.dokka)
            }

            configureAndroid<LibraryExtension>()

            configureKotlin<KotlinMultiplatformExtension>()

            kotlin<KotlinMultiplatformExtension> {
                androidTarget {
                    publishAllLibraryVariants()
                }
                jvm()

                // iPhone ipad
                iosArm64()
                // ios Simulator on Apple silicon
                iosSimulatorArm64()
                // MacOS
                iosX64()
            }
        }
    }
}
