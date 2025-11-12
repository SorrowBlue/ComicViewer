import com.android.build.api.dsl.DynamicFeatureExtension
import com.sorrowblue.comicviewer.configureAboutLibraries
import com.sorrowblue.comicviewer.configureAndroid
import com.sorrowblue.comicviewer.configureKotlin
import com.sorrowblue.comicviewer.configureKotlinMultiplatform
import com.sorrowblue.comicviewer.configureLicensee
import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.kotlin
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformAndroidDynamicFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.kotlin.multiplatform)
                id(libs.plugins.android.dynamicFeature)
                id(libs.plugins.comicviewer.android.lint)
                id(libs.plugins.comicviewer.detekt)
                id(libs.plugins.comicviewer.dokka)
                id(libs.plugins.licensee)
                id(libs.plugins.aboutlibraries)
            }

            configureKotlin<KotlinMultiplatformExtension>()
            configureKotlinMultiplatform()
            configureAndroid<DynamicFeatureExtension>()
            configureLicensee()
            configureAboutLibraries()

            kotlin<KotlinMultiplatformExtension> {
                sourceSets.commonMain.dependencies {
                    implementation(project(":framework:common"))
                }
            }
        }
    }
}
