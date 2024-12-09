import com.android.build.api.dsl.DynamicFeatureExtension
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
internal class AndroidDynamicFeatureConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.android.dynamicFeature)
                id(libs.plugins.kotlin.android)
                id(libs.plugins.comicviewer.android.lint)
                id(libs.plugins.comicviewer.detekt)
                id(libs.plugins.comicviewer.dokka)
            }

            configureAndroid<DynamicFeatureExtension>()

            configureKotlin<KotlinAndroidProjectExtension>()

            android<DynamicFeatureExtension> {
                defaultConfig {
                    proguardFile("consumer-rules.pro")
                }
            }

            dependencies {
                implementation(project(":framework:common"))
                applyTestImplementation(this@with)
            }
        }
    }
}
