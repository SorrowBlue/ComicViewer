import com.android.build.api.dsl.LibraryExtension
import com.sorrowblue.comicviewer.configureKotlinAndroid
import com.sorrowblue.comicviewer.implementation
import com.sorrowblue.comicviewer.kotlin
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.testDebugImplementation
import com.sorrowblue.comicviewer.testImplementation
import com.sorrowblue.comicviewer.testInternalImplementation
import com.sorrowblue.comicviewer.testPrereleaseImplementation
import com.sorrowblue.comicviewer.testReleaseImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

internal class AndroidLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("comicviewer.android.lint")
                apply("comicviewer.android.dokka")
                apply("org.jetbrains.kotlinx.kover")
            }

            kotlin {
                jvmToolchain(17)
                compilerOptions {
                    freeCompilerArgs.add("-Xcontext-receivers")
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig {
                    consumerProguardFiles("consumer-rules.pro")
                }
            }


            dependencies {
                implementation(libs.squareup.logcat)
            }
        }
    }
}

internal class AndroidLibraryTestConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {

            dependencies {
                testDebugImplementation(libs.androidx.compose.ui.testManifest)
                testPrereleaseImplementation(libs.androidx.compose.ui.testManifest)
                testInternalImplementation(libs.androidx.compose.ui.testManifest)
                testReleaseImplementation(libs.androidx.compose.ui.testManifest)
                testImplementation(libs.androidx.compose.ui.testManifest)
                testImplementation(libs.kotlinx.coroutines.test)
                testImplementation(libs.androidx.test.ext.junitKtx)
                testImplementation(libs.androidx.test.ext.truth)
                testImplementation(libs.robolectric)
            }
        }
    }
}
