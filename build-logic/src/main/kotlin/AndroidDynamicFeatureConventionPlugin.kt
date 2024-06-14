import com.android.build.api.dsl.DynamicFeatureExtension
import com.sorrowblue.comicviewer.apply
import com.sorrowblue.comicviewer.applyTestImplementation
import com.sorrowblue.comicviewer.configureKotlin
import com.sorrowblue.comicviewer.configureKotlinAndroid
import com.sorrowblue.comicviewer.implementation
import com.sorrowblue.comicviewer.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

internal class AndroidDynamicFeatureConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.plugins.android.dynamicFeature)
                apply(libs.plugins.kotlin.android)
                apply(libs.plugins.comicviewer.detekt)
                apply(libs.plugins.comicviewer.dokka)
            }

            extensions.configure<DynamicFeatureExtension> {
                configureKotlinAndroid(this)
                defaultConfig {
                    proguardFile("consumer-rules.pro")
                }
            }

            extensions.configure<KotlinAndroidProjectExtension> {
                configureKotlin(this)
            }

            dependencies {
                implementation(libs.squareup.logcat)
                applyTestImplementation()
            }
        }
    }
}
