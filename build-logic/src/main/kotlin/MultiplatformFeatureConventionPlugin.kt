import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class MultiplatformFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.comicviewer.multiplatformLibrary)
                id(libs.plugins.comicviewer.multiplatformCompose)
                id(libs.plugins.comicviewer.di)
            }
            configure<KotlinMultiplatformExtension> {
                configure<KotlinMultiplatformAndroidLibraryTarget> {
                    androidResources.enable = true
                }
                sourceSets.commonMain.dependencies {
                    implementation(project(":framework:designsystem"))
                    implementation(project(":framework:ui"))
                    implementation(project(":domain:usecase"))

                    // Image
                    implementation(libs.coil3.compose)
                    // Paging
                    implementation(libs.androidx.pagingCommon)
                }
            }
        }
    }
}
