import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.kotlin
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.composeMultiplatform)
                id(libs.plugins.kotlin.compose)
            }

            kotlin<KotlinMultiplatformExtension> {
                sourceSets.commonMain.dependencies {
                    implementation(libs.kotlinx.serialization.json)
                }
            }
        }
    }
}
