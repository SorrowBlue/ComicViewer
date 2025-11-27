import com.sorrowblue.comicviewer.id
import com.sorrowblue.comicviewer.libs
import com.sorrowblue.comicviewer.plugins
import dev.detekt.gradle.Detekt
import dev.detekt.gradle.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.gradle.language.base.plugins.LifecycleBasePlugin

internal class DetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins {
                id(libs.plugins.detekt)
            }

            dependencies {
                detektPlugins(libs.detekt.compose)
                detektPlugins(libs.detekt.ktlintWrapper)
            }

            configure<DetektExtension> {
                buildUponDefaultConfig.set(true)
                autoCorrect.set(true)
                basePath.set(rootProject.projectDir)
                config.setFrom("${rootProject.projectDir}/config/detekt/detekt.yml")
            }

            tasks.withType<Detekt>().configureEach {
                reports {
                    sarif.required.set(true)
                    html.required.set(false)
                    markdown.required.set(false)
                    checkstyle.required.set(false)
                }
                exclude {
                    it.file.path.run { contains("generated") || contains("buildkonfig") }
                }
            }

            mapOf(
                "detektAndroidAll" to "(?i)^(?!.*(SourceSet|metadata)).*android.*$".toRegex(),
                "detektDesktopAll" to "(?i)^(?!.*(SourceSet|metadata)).*desktop.*$".toRegex(),
                "detektIosAll" to "(?i)^(?!.*(metadata)).*ios.*$".toRegex(),
            ).forEach { (taskName, regex) ->
                tasks.register(taskName) {
                    group = LifecycleBasePlugin.VERIFICATION_GROUP
                    dependsOn(
                        tasks
                            .withType<Detekt>()
                            .matching { detekt -> detekt.name.contains(regex) },
                    )
                }
            }
        }
    }

    private val Project.detektPlugins get() = configurations.getByName("detektPlugins")
}
