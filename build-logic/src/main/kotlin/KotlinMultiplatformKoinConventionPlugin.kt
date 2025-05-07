import com.sorrowblue.comicviewer.hasPlugin
import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.kotlin
import com.sorrowblue.comicviewer.ksp
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
                id(libs.plugins.kotlinMultiplatform)
                id(libs.plugins.google.ksp)
            }

            kotlin<KotlinMultiplatformExtension> {
                sourceSets.commonMain.dependencies {
                    implementation(libs.koin.core)
                    implementation(libs.koin.annotations)
                }
            }

            dependencies {
                add("kspAndroid", libs.koin.kspCompiler)
                if (pluginManager.hasPlugin(libs.plugins.android.kotlin.multiplatform.library)) {
                    add("kspAndroidHostTest", libs.koin.kspCompiler)
                    add("kspAndroidDeviceTest", libs.koin.kspCompiler)
                } else {
                    add("kspAndroidTest", libs.koin.kspCompiler)
                }
                add("kspIosX64", libs.koin.kspCompiler)
                add("kspIosX64Test", libs.koin.kspCompiler)
                add("kspIosArm64", libs.koin.kspCompiler)
                add("kspIosArm64Test", libs.koin.kspCompiler)
                add("kspIosSimulatorArm64", libs.koin.kspCompiler)
                add("kspIosSimulatorArm64Test", libs.koin.kspCompiler)
                add("kspDesktop", libs.koin.kspCompiler)
                add("kspDesktopTest", libs.koin.kspCompiler)
            }

            ksp {
                arg("KOIN_USE_COMPOSE_VIEWMODEL", "true")
                arg("KOIN_DEFAULT_MODULE", "false")
                arg("KOIN_CONFIG_CHECK", "false")
            }
        }
    }
}
