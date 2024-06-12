import com.google.devtools.ksp.gradle.KspExtension
import com.sorrowblue.comicviewer.apply
import com.sorrowblue.comicviewer.implementation
import com.sorrowblue.comicviewer.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

internal class KoinConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.plugins.google.ksp)
            }

            dependencies {
                implementation(platform(libs.koin.bom))
                implementation(libs.koin.compose)
                implementation(libs.koin.android)
            }
            extensions.configure<KspExtension> {
                arg("KOIN_CONFIG_CHECK", "false")
            }
        }
    }
}
