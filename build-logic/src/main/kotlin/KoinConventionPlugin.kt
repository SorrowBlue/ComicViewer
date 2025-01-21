import com.google.devtools.ksp.gradle.KspExtension
import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.implementation
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

@Suppress("unused")
internal class KoinConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.google.ksp)
            }

            dependencies {
                implementation(platform(libs.koin.bom))
                implementation(libs.koin.compose)
                implementation(libs.koin.android)
            }
            configure<KspExtension> {
                arg("KOIN_CONFIG_CHECK", "false")
                arg("KOIN_USE_COMPOSE_VIEWMODEL", "true")
            }
        }
    }
}
