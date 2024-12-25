import com.sorrowblue.comicviewer.detektPlugins
import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.plugins
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

@Suppress("unused")
internal class DetektConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.detekt)
            }

            dependencies {
                detektPlugins(libs.nlopez.compose.rules.detekt)
                detektPlugins(libs.arturbosch.detektFormatting)
            }

            configure<DetektExtension> {
                buildUponDefaultConfig = true
                autoCorrect = true
                basePath = rootProject.projectDir.absolutePath
                config.setFrom("${rootProject.projectDir}/config/detekt/detekt.yml")
            }

            val reportMerge = rootProject.tasks.withType(ReportMergeTask::class)
            tasks.withType<Detekt>().configureEach {
                reports {
                    sarif.required.set(true)
                    html.required.set(false)
                    md.required.set(false)
                    txt.required.set(false)
                    xml.required.set(false)
                }
                finalizedBy(reportMerge)
            }
            reportMerge.configureEach {
                input.from(tasks.withType<Detekt>().map(Detekt::sarifReportFile))
            }
        }
    }
}
