import com.android.build.api.dsl.ApplicationExtension
import com.sorrowblue.comicviewer.ComicBuildType
import com.sorrowblue.comicviewer.android
import com.sorrowblue.comicviewer.configureAboutLibraries
import com.sorrowblue.comicviewer.configureAndroid
import com.sorrowblue.comicviewer.configureKotlin
import com.sorrowblue.comicviewer.configureKotlinMultiplatform
import com.sorrowblue.comicviewer.configureLicensee
import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.kotlin
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.kotlinMultiplatform)
                id(libs.plugins.android.application)
                id(libs.plugins.comicviewer.android.lint)
                id(libs.plugins.comicviewer.detekt)
                id(libs.plugins.licensee)
                id(libs.plugins.aboutlibraries)
            }

            configureKotlin<KotlinMultiplatformExtension>()
            configureKotlinMultiplatform()
            configureAndroid<ApplicationExtension>()

            kotlin<KotlinMultiplatformExtension> {
                sourceSets.commonMain.dependencies {
                    implementation(project(":framework:common"))
                }
            }

            android<ApplicationExtension> {
                signingConfigs {
                    val androidSigningDebugStoreFile: String? by project
                    if (!androidSigningDebugStoreFile.isNullOrEmpty()) {
                        named(ComicBuildType.DEBUG.display) {
                            val androidSigningDebugStorePassword: String? by project
                            val androidSigningDebugKeyAlias: String? by project
                            val androidSigningDebugKeyPassword: String? by project
                            storeFile = file(androidSigningDebugStoreFile!!)
                            storePassword = androidSigningDebugStorePassword
                            keyAlias = androidSigningDebugKeyAlias
                            keyPassword = androidSigningDebugKeyPassword
                        }
                    } else {
                        logger.warn("androidSigningDebugStoreFile not found")
                    }

                    val androidSigningReleaseStoreFile: String? by project
                    if (!androidSigningReleaseStoreFile.isNullOrEmpty()) {
                        val release = create(ComicBuildType.RELEASE.display) {
                            val androidSigningReleaseStorePassword: String? by project
                            val androidSigningReleaseKeyAlias: String? by project
                            val androidSigningReleaseKeyPassword: String? by project
                            storeFile = file(androidSigningReleaseStoreFile!!)
                            storePassword = androidSigningReleaseStorePassword
                            keyAlias = androidSigningReleaseKeyAlias
                            keyPassword = androidSigningReleaseKeyPassword
                        }
                        create(ComicBuildType.PRERELEASE.display) {
                            initWith(release)
                        }
                        create(ComicBuildType.INTERNAL.display) {
                            initWith(release)
                        }
                    } else {
                        logger.warn("androidSigningReleaseStoreFile not found")
                    }
                }
            }

            configureLicensee()
            configureAboutLibraries()
        }
    }
}
