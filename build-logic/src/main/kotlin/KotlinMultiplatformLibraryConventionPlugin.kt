import com.android.build.api.dsl.androidLibrary
import com.sorrowblue.comicviewer.configureKotlin
import com.sorrowblue.comicviewer.configureKotlinMultiplatform
import com.sorrowblue.comicviewer.configureLicensee
import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.kotlin
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.kotlinMultiplatform)
                id(libs.plugins.android.kotlin.multiplatform.library)
                id(libs.plugins.comicviewer.android.lint)
                id(libs.plugins.comicviewer.detekt)
                id(libs.plugins.comicviewer.dokka)
                id(libs.plugins.licensee)
            }
            configureKotlinMultiplatform {
                androidLibrary {
                    experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
                    optimization {
                        consumerKeepRules.file("consumer-rules.pro")
                    }
                    withHostTest {
                        isIncludeAndroidResources = true
                    }
                    withDeviceTest {
                        instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    }
                }
                sourceSets {
                    commonMain {
                        dependencies {
                            if (project.name != "common" || project.parent?.name != "framework") {
                                implementation(project(":framework:common"))
                            }
                        }
                    }
                }
            }
            configureLicensee()
        }
    }
}
