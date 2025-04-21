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

internal class DetektConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.detekt)
            }
            val detektPlugins2 = configurations.getByName("detektPlugins")
            dependencies {
                detektPlugins2(libs.nlopez.compose.rules.detekt)
                detektPlugins2(libs.arturbosch.detektFormatting)
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
                exclude {
                    it.file.path.run { contains("generated") || contains("buildkonfig") }
                }
            }
            reportMerge.configureEach {
                input.from(tasks.withType<Detekt>().map(Detekt::sarifReportFile))
            }

            tasks.register("detektAndroidAll") {
                group = "verification"
                dependsOn(
                    tasks.withType<Detekt>()
                        .matching { detekt -> detekt.name.contains("(?i)^(?!.*metadata).*android.*$".toRegex()) }
                )
            }
            tasks.register("detektDesktopAll") {
                group = "verification"
                dependsOn(
                    tasks.withType<Detekt>()
                        .matching { detekt -> detekt.name.contains("(?i)^(?!.*metadata).*desktop.*$".toRegex()) }
                )
            }
            tasks.register("detektIosAll") {
                group = "verification"
                dependsOn(
                    tasks.withType<Detekt>()
                        .matching { detekt -> detekt.name.contains("(?i)^(?!.*metadata).*ios.*$".toRegex()) }
                )
            }
            tasks.register("detektMetadataAll") {
                group = "verification"
                dependsOn(
                    tasks.withType<Detekt>()
                        .matching { detekt -> detekt.name.contains("(?i)^.*metadata.*$".toRegex()) }
                )
            }
        }
    }
}
