import com.android.build.api.dsl.DynamicFeatureExtension
import com.sorrowblue.comicviewer.configureKotlinAndroid
import com.sorrowblue.comicviewer.implementation
import com.sorrowblue.comicviewer.kotlin
import com.sorrowblue.comicviewer.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

internal class AndroidDynamicFeatureConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.dynamic-feature")
                apply("org.jetbrains.kotlin.android")
                apply("comicviewer.android.lint")
                apply("comicviewer.android.dokka")
            }

            kotlin {
                jvmToolchain(17)
                compilerOptions {
                    freeCompilerArgs.add("-Xcontext-receivers")
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }

            extensions.configure<DynamicFeatureExtension> {
                configureKotlinAndroid(this)
                defaultConfig {
                    proguardFile("consumer-rules.pro")
                }
            }

            dependencies {
                implementation(libs.findLibrary("squareup.logcat").get())
            }
        }
    }
}
