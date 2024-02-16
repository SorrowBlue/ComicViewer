import com.android.build.api.dsl.CommonExtension
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.testImplementation
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

fun Project.dependTestImplementation() {
    dependencies {
        testImplementation(libs.findLibrary("androidx-test-ext-junit-ktx").get())
        testImplementation(libs.findLibrary("robolectric").get())
        testImplementation(libs.findLibrary("androidx-test-ext-truth").get())
        testImplementation(libs.findLibrary("kotlinx-coroutines-test").get())
    }
}

fun CommonExtension<*, *, *, *, *, *>.testOption() {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}
