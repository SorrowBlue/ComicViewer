import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.kotlin
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformDiConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.kotlinMultiplatform)
                id(libs.plugins.google.ksp)
                id(libs.plugins.google.dagger.hilt)
            }

            kotlin<KotlinMultiplatformExtension> {
                sourceSets.androidMain.dependencies {
                    implementation(libs.google.dagger.hilt.android)
                }
            }

            dependencies {
                add("kspAndroid", libs.google.dagger.hilt.compiler)
            }
        }
    }
}
