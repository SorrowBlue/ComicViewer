import com.google.devtools.ksp.gradle.KspExtension
import com.sorrowblue.comicviewer.apply
import com.sorrowblue.comicviewer.configureAndroidCompose
import com.sorrowblue.comicviewer.implementation
import com.sorrowblue.comicviewer.ksp
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.parentName
import com.sorrowblue.comicviewer.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

internal class ComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.plugins.kotlin.compose)
                apply(libs.plugins.google.ksp)
            }

            configureAndroidCompose()

            dependencies {
                implementation(libs.compose.destinations.core)
                ksp(libs.compose.destinations.ksp)

                testImplementation(platform(libs.androidx.compose.bom))
                testImplementation(libs.androidx.compose.ui.testManifest)
                testImplementation(libs.androidx.compose.ui.testJunit4)
            }

            extensions.configure<KspExtension> {
                arg("compose-destinations.codeGenPackageName", "com.sorrowblue.${parentName()}")
            }
        }
    }
}
