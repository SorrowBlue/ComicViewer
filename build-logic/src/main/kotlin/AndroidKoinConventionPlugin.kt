import com.sorrowblue.comicviewer.implementation
import com.sorrowblue.comicviewer.ksp
import com.sorrowblue.comicviewer.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

internal class AndroidKoinConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.google.devtools.ksp")

            dependencies {
                implementation(platform(libs.findLibrary("koin-bom").get()))
                implementation(libs.findLibrary("koin-core").get())
                implementation(libs.findLibrary("koin-annotations").get())
                implementation(libs.findLibrary("koin-compose").get())
                implementation(libs.findLibrary("koin-workmanager").get())
                ksp(libs.findLibrary("koin-kspCompiler").get())
            }
            extensions.configure<com.google.devtools.ksp.gradle.KspExtension> {
                arg("KOIN_CONFIG_CHECK","true")
            }
        }
    }
}
