import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.kotlin
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformKoinConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.kotlin.multiplatform)
                id(libs.plugins.google.ksp)
            }

            kotlin<KotlinMultiplatformExtension> {
                sourceSets.commonMain.dependencies {
                    implementation(libs.koin.core)
                    implementation(libs.koin.annotations)
                    implementation(libs.koin.jsr330)
                }
                sourceSets.androidUnitTest.dependencies {
                    implementation(libs.koin.androidxCompose)
                }
            }

            dependencies {
                add("kspCommonMainMetadata", libs.koin.kspCompiler)
                add("kspAndroid", libs.koin.kspCompiler)
                add("kspAndroidTest", libs.koin.kspCompiler)
                add("kspIosX64", libs.koin.kspCompiler)
                add("kspIosX64Test", libs.koin.kspCompiler)
                add("kspIosArm64", libs.koin.kspCompiler)
                add("kspIosArm64Test", libs.koin.kspCompiler)
                add("kspIosSimulatorArm64", libs.koin.kspCompiler)
                add("kspIosSimulatorArm64Test", libs.koin.kspCompiler)
                add("kspDesktop", libs.koin.kspCompiler)
                add("kspDesktopTest", libs.koin.kspCompiler)
            }
        }
    }
}
