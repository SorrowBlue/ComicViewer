import com.android.build.api.dsl.LibraryExtension
import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformFeatureConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.comicviewer.kotlinMultiplatform.library)
                id(libs.plugins.comicviewer.kotlinMultiplatform.compose)
                id(libs.plugins.comicviewer.kotlinMultiplatform.koin)
                id(libs.plugins.kotlin.serialization)
            }
            configure<KotlinMultiplatformExtension> {
                sourceSets.commonMain.dependencies {
                    implementation(project(":framework:designsystem"))
                    implementation(project(":framework:ui"))
                    implementation(project(":domain:usecase"))
                    implementation(libs.cmpdestinations)
                }
            }
            configure<LibraryExtension> {
            }
            dependencies {
                add("kspAndroid", libs.cmpdestinations.ksp)
                add("kspIosX64", libs.cmpdestinations.ksp)
                add("kspIosArm64", libs.cmpdestinations.ksp)
                add("kspIosSimulatorArm64", libs.cmpdestinations.ksp)
                add("kspDesktop", libs.cmpdestinations.ksp)
            }
        }
    }
}
