// import com.android.build.api.dsl.ApplicationExtension
import com.sorrowblue.comicviewer.ComicBuildType
import com.sorrowblue.comicviewer.configureAboutLibraries
// import com.sorrowblue.comicviewer.configureAndroid
import com.sorrowblue.comicviewer.configureKotlin
import com.sorrowblue.comicviewer.configureKotlinMultiplatform
import com.sorrowblue.comicviewer.configureLicensee
import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.kotlin
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
// import org.gradle.kotlin.dsl.configure
// import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.kotlin.multiplatform)
                apply("com.android.kotlin.multiplatform")
                id(libs.plugins.comicviewer.android.lint)
                id(libs.plugins.comicviewer.detekt)
                id(libs.plugins.comicviewer.dokka)
                id(libs.plugins.licensee)
                id(libs.plugins.aboutlibraries)
            }

            configureKotlin<KotlinMultiplatformExtension>()
            configureKotlinMultiplatform(configureAndroidTarget = false)
            // TODO: AGP 9.0 - Need to find alternative way to configure Android application settings
            // configure<ApplicationExtension> { configureAndroid(this) }
            configureLicensee()
            configureAboutLibraries()

            kotlin<KotlinMultiplatformExtension> {
                sourceSets.commonMain.dependencies {
                    implementation(project(":framework:common"))
                }
            }

            // TODO: AGP 9.0 - Need to find alternative way to configure signing configs
            // configure<ApplicationExtension> {
            //     signingConfigs {
            //         ...
            //     }
            // }
        }
    }
}
