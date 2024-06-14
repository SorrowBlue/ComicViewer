import dev.iurysouza.modulegraph.Orientation
import java.util.Locale
import org.jetbrains.dokka.gradle.DokkaMultiModuleTask

plugins {
    alias(libs.plugins.detekt)
    alias(libs.plugins.versions)
    alias(libs.plugins.dokka)
    alias(libs.plugins.modulegraph)
    alias(libs.plugins.kotlinx.kover)
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.dynamicFeature) apply false
    alias(libs.plugins.androidx.navigation.safeargs.kotlin) apply false
    alias(libs.plugins.androidx.room) apply false
    alias(libs.plugins.google.dagger.hilt) apply false
    alias(libs.plugins.google.ksp) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.aboutlibraries) apply false
    alias(libs.plugins.roborazzi) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

fun isNonStable(version: String): Boolean {
    val stableKeyword =
        listOf("RELEASE", "FINAL", "GA").any { version.uppercase(Locale.getDefault()).contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

tasks.named(
    "dependencyUpdates",
    com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask::class.java
).configure {
    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}

tasks.named("detekt", io.gitlab.arturbosch.detekt.Detekt::class.java).configure {
    reports {
        html.required = true
        md.required = false
        sarif.required = true
        txt.required = false
        xml.required = false
    }
}
val reportMerge by tasks.registering(io.gitlab.arturbosch.detekt.report.ReportMergeTask::class) {
    output.set(rootProject.layout.buildDirectory.file("reports/detekt/merge.sarif"))
}

subprojects {

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        finalizedBy(reportMerge)
    }

    reportMerge {
        input.from(tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().map { it.sarifReportFile })
    }
}
plugins.withId("org.jetbrains.dokka") {
    tasks.withType<DokkaMultiModuleTask>().configureEach {
        notCompatibleWithConfigurationCache("https://github.com/Kotlin/dokka/issues/1217")
        outputDirectory.set(layout.projectDirectory.dir("docs/dokka"))
    }
}
afterEvaluate {
    val task = tasks.named("createModuleGraph")
    task.configure {
        doNotTrackState("Failed to create MD5 hash for file content.")
    }
}
moduleGraphConfig {
    graph("${rootDir}/README2.md", "## Domain") {
        focusedModulesRegex = ".*(domain).*"
        rootModulesRegex = ".*(domain).*"
        excludedModulesRegex = ".*(feature).*"
    }
    graph("${rootDir}/README2.md", "## Data") {
        focusedModulesRegex = ".*(data).*"
        rootModulesRegex = ".*(data).*"
        orientation = Orientation.RIGHT_TO_LEFT
        excludedModulesRegex = ".*(feature|di|model|app|framework).*"
        excludedConfigurationsRegex = "api"
    }
//    focusedModulesRegex.set(".*(domain).*")
//    this.rootModulesRegex.set(".*(data).*")
//    setStyleByModuleType.set(true)
}

tasks.updateDaemonJvm {
    @Suppress("UnstableApiUsage")
    jvmVersion = JavaVersion.VERSION_17
}
