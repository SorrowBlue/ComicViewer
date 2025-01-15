import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.implementation
import com.sorrowblue.comicviewer.kotlin
import com.sorrowblue.comicviewer.ksp
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

class KotlinMultiplatformKoinConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.kotlinMultiplatform)
                id(libs.plugins.google.ksp)
            }

            kotlin<KotlinMultiplatformExtension> {
                sourceSets.commonMain.dependencies {
                    implementation(project.dependencies.platform(libs.koin.bom))
                    implementation(libs.koin.core)

                    implementation(libs.koin.annotations)
                }

                sourceSets.commonMain.configure {
                    kotlin.srcDir(layout.buildDirectory.dir("generated/ksp/metadata/commonMain/kotlin"))
                }

                sourceSets.androidMain.dependencies {
                    implementation(libs.koin.android)
                }
            }

            dependencies {
                add("kspCommonMainMetadata", libs.koin.kspCompiler)
                add("kspAndroid", libs.koin.kspCompiler)
                add("kspIosX64", libs.koin.kspCompiler)
                add("kspIosArm64", libs.koin.kspCompiler)
                add("kspIosSimulatorArm64", libs.koin.kspCompiler)
                add("kspDesktop", libs.koin.kspCompiler)
            }

            ksp {
                arg("KOIN_USE_COMPOSE_VIEWMODEL", "true")
                arg("KOIN_CONFIG_CHECK", "false")
            }

            // Trigger Common Metadata Generation from Native tasks
//            project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
//                if (name != "kspCommonMainKotlinMetadata") {
//                    dependsOn("kspCommonMainKotlinMetadata")
//                }
//            }
        }
    }
}
