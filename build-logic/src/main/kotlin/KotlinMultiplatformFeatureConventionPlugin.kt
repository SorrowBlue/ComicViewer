import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformFeatureConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.comicviewer.kotlinMultiplatform.library)
                id(libs.plugins.comicviewer.kotlinMultiplatform.compose)
                id(libs.plugins.comicviewer.kotlinMultiplatform.koin)
            }
            configure<KotlinMultiplatformExtension> {
                sourceSets.commonMain.dependencies {
                    implementation(project(":framework:designsystem"))
                    implementation(project(":framework:ui"))
                    implementation(project(":domain:usecase"))

                    // Material3
                    val compose = extensions.getByType<ComposePlugin.Dependencies>()
                    implementation(compose.material3)
                    // Image
                    implementation(libs.coil3.compose)
                    // Paging
                    implementation(libs.androidx.paging.common)
                    // Di
                    implementation(libs.koin.composeViewModel)
                }
            }
        }
    }
}
