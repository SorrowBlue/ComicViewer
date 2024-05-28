import com.android.build.api.dsl.LibraryExtension
import com.sorrowblue.comicviewer.configureKotlinAndroid
import com.sorrowblue.comicviewer.debugImplementation
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
                implementation(libs.findLibrary("squareup.logcat").get())
            }
        }
    }
}
internal class AndroidLibraryTestConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {

            dependencies {
                testDebugImplementation(libs.findLibrary("androidx-compose-ui-testManifest").get())
                testPrereleaseImplementation(libs.findLibrary("androidx-compose-ui-testManifest").get())
                testInternalImplementation(libs.findLibrary("androidx-compose-ui-testManifest").get())
                testReleaseImplementation(libs.findLibrary("androidx-compose-ui-testManifest").get())
                testImplementation(libs.findLibrary("androidx-compose-ui-testJunit4").get())
                testImplementation(libs.findLibrary("kotlinx-coroutines-test").get())
                testImplementation(libs.findLibrary("androidx-test-ext-junitKtx").get())
                testImplementation(libs.findLibrary("androidx-test-ext-truth").get())
                testImplementation(libs.findLibrary("robolectric").get())
            }
        }
    }
}
