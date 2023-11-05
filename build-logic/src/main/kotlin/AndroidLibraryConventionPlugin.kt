import com.android.build.api.dsl.LibraryExtension
import com.sorrowblue.comicviewer.configureKotlinAndroid
import com.sorrowblue.comicviewer.implementation
import com.sorrowblue.comicviewer.kotlin
import com.sorrowblue.comicviewer.libs
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

internal class AndroidLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("io.gitlab.arturbosch.detekt")
            }

            kotlin {
                jvmToolchain(17)
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig {
                    consumerProguardFiles("consumer-rules.pro")
                }
            }

            dependencies {
                add("detektPlugins", libs.findLibrary("nlopez.compose.rules.detekt").get())
                add("detektPlugins", libs.findLibrary("arturbosch.detektFormatting").get())
                implementation(libs.findLibrary("squareup.logcat").get())
            }

            extensions.configure<DetektExtension>("detekt") {
                buildUponDefaultConfig = true
                autoCorrect = true
                config.setFrom("${rootProject.projectDir}/config/detekt/detekt.yml")
            }
        }
    }
}
