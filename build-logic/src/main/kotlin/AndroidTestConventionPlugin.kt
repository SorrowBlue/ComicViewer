import com.sorrowblue.comicviewer.androidTestImplementation
import com.sorrowblue.comicviewer.apply
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.testDebugImplementation
import com.sorrowblue.comicviewer.testImplementation
import com.sorrowblue.comicviewer.testInternalImplementation
import com.sorrowblue.comicviewer.testPrereleaseImplementation
import com.sorrowblue.comicviewer.testReleaseImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal class AndroidTestConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {

            with(pluginManager) {
                apply(libs.plugins.kotlinx.kover)
            }

            dependencies {
                testDebugImplementation(platform(libs.androidx.compose.bom))
                testDebugImplementation(libs.androidx.compose.ui.testManifest)
                testPrereleaseImplementation(platform(libs.androidx.compose.bom))
                testPrereleaseImplementation(libs.androidx.compose.ui.testManifest)
                testInternalImplementation(platform(libs.androidx.compose.bom))
                testInternalImplementation(libs.androidx.compose.ui.testManifest)
                testReleaseImplementation(platform(libs.androidx.compose.bom))
                testReleaseImplementation(libs.androidx.compose.ui.testManifest)
                testImplementation(platform(libs.androidx.compose.bom))
                testImplementation(libs.androidx.compose.ui.testManifest)
                testImplementation(libs.kotlinx.coroutines.test)
                testImplementation(libs.androidx.test.ext.junitKtx)
                testImplementation(libs.androidx.test.ext.truth)
                testImplementation(libs.robolectric)
                androidTestImplementation(libs.kotlinx.coroutines.test)
                androidTestImplementation(libs.androidx.test.ext.junitKtx)
                androidTestImplementation(libs.androidx.test.ext.truth)
                androidTestImplementation(libs.androidx.test.runner)
            }
        }
    }
}
