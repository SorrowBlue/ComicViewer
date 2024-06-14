import com.android.build.api.dsl.LibraryExtension
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

@Suppress("unused")
internal class AndroidLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.plugins.android.library)
                apply(libs.plugins.kotlin.android)
                apply(libs.plugins.comicviewer.detekt)
                apply(libs.plugins.comicviewer.dokka)
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig {
                    consumerProguardFiles("consumer-rules.pro")
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
