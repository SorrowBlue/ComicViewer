import com.android.build.api.dsl.ApplicationExtension
import com.sorrowblue.comicviewer.configureKotlinAndroid
import com.sorrowblue.comicviewer.implementation
import com.sorrowblue.comicviewer.kotlin
import com.sorrowblue.comicviewer.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

internal class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("io.gitlab.arturbosch.detekt")
            }

            kotlin {
                jvmToolchain(17)
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
            }

            dependencies {
                implementation(libs.findLibrary("squareup.logcat").get())
            }
        }
    }
}
