import com.android.build.api.dsl.LibraryExtension
import com.sorrowblue.comicviewer.configureAndroid
import com.sorrowblue.comicviewer.configureKotlin
import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.kotlin
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

@Suppress("unused")
internal class AndroidKotlinMultiplatformConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.kotlinMultiplatform)
                id(libs.plugins.android.library)
                id(libs.plugins.comicviewer.android.lint)
                id(libs.plugins.comicviewer.detekt)
                id(libs.plugins.comicviewer.dokka)
                id(libs.plugins.google.dagger.hilt)
                id(libs.plugins.google.ksp)
            }

            configureAndroid<LibraryExtension>()

            configureKotlin<KotlinMultiplatformExtension>()

            kotlin<KotlinMultiplatformExtension> {
                androidTarget {
                    publishAllLibraryVariants()
                }
                jvm("desktop")

                // iPhone ipad
                iosArm64()
                // ios Simulator on Apple silicon
                iosSimulatorArm64()
                // MacOS
                iosX64()
                applyDefaultHierarchyTemplate()

                sourceSets.commonMain.dependencies {
                    if (this@with.name != "common" || parent?.name != "framework") {
                        implementation(project(":framework:common"))
                    }
                    implementation(project.dependencies.platform(libs.koin.bom))
                    implementation(libs.koin.core)
                    implementation(libs.koin.annotations)
                }
                sourceSets.commonMain.configure {
                    kotlin.srcDir(layout.buildDirectory.dir("generated/ksp/metadata/commonMain/kotlin"))
                }

                val noAndroid by sourceSets.creating {
                    dependsOn(sourceSets.commonMain.get())
                    dependencies {
                        implementation(project.dependencies.platform(libs.koin.bom))
                        implementation(libs.koin.core)
                    }
                }
                sourceSets.androidMain.dependencies {
                    implementation(libs.google.dagger.hilt.android)
                }
                sourceSets.iosMain {
                    dependsOn(noAndroid)
                }
                val desktopMain by sourceSets.getting {
                    dependsOn(noAndroid)
                }
            }

            dependencies {
                add("kspCommonMainMetadata", libs.koin.kspCompiler)
                add("kspIosX64", libs.koin.kspCompiler)
                add("kspIosArm64", libs.koin.kspCompiler)
                add("kspIosSimulatorArm64", libs.koin.kspCompiler)
                add("kspDesktop", libs.koin.kspCompiler)
                add("kspAndroid", libs.google.dagger.hilt.compiler)
            }

            // Trigger Common Metadata Generation from Native tasks
            project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
                if (name != "kspCommonMainKotlinMetadata") {
                    dependsOn("kspCommonMainKotlinMetadata")
                }
            }
        }
    }
}
