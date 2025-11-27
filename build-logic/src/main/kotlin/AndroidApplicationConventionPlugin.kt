import com.android.build.api.dsl.ApplicationExtension
import com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension
import com.sorrowblue.comicviewer.ComicBuildType
import com.sorrowblue.comicviewer.configureAboutLibraries
import com.sorrowblue.comicviewer.configureKotlin
import com.sorrowblue.comicviewer.configureLicensee
import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.implementation
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.androidApplication)

                id(libs.plugins.comicviewer.androidLint)
                id(libs.plugins.comicviewer.detekt)
                id(libs.plugins.comicviewer.dokka)

                id(libs.plugins.aboutlibraries)
                id(libs.plugins.licensee)
            }

            configureKotlin<KotlinAndroidProjectExtension>()
            configure<ApplicationExtension> {
                defaultConfig {
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro",
                    )
                }
                signingConfigs {
                    val androidSigningDebugStoreFile: String? by project
                    if (!androidSigningDebugStoreFile.isNullOrEmpty()) {
                        named(ComicBuildType.DEBUG.display) {
                            val androidSigningDebugStorePassword: String? by project
                            val androidSigningDebugKeyAlias: String? by project
                            val androidSigningDebugKeyPassword: String? by project
                            storeFile = file(requireNotNull(androidSigningDebugStoreFile))
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
                            storeFile = file(requireNotNull(androidSigningReleaseStoreFile))
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

            dependencies {
                implementation(project(":framework:common"))
            }
            configureLicensee()

            configureAboutLibraries()
            configure<AboutLibrariesExtension> {
                export {
                    outputFile.set(file("src/main/res/raw/aboutlibraries.json"))
                }
            }
        }
    }
}
