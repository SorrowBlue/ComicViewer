import dev.iurysouza.modulegraph.Orientation
import io.gitlab.arturbosch.detekt.Detekt
import java.util.Locale

plugins {
    alias(libs.plugins.detekt)
    alias(libs.plugins.versions)
    alias(libs.plugins.gitVersioning)
    alias(libs.plugins.dokka)
    alias(libs.plugins.modulegraph)
    alias(libs.plugins.kotlinx.kover)
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.dynamicFeature) apply false
    alias(libs.plugins.androidx.room) apply false
    alias(libs.plugins.google.ksp) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.licensee) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.aboutlibraries) apply false
}

dependencies {
    dokka(projects.data.coil)
    dokka(projects.data.database)
    dokka(projects.data.datastore)
    dokka(projects.data.di)
    dokka(projects.data.reader.document)
    dokka(projects.data.reader.zip)
    dokka(projects.data.storage.client)
    dokka(projects.data.storage.smb)
    dokka(projects.domain.model)
    dokka(projects.domain.reader)
    dokka(projects.domain.service)
    dokka(projects.domain.usecase)
    dokka(projects.feature.authentication)
    dokka(projects.feature.book)
    dokka(projects.feature.bookshelf)
    dokka(projects.feature.bookshelf.edit)
    dokka(projects.feature.bookshelf.info)
    dokka(projects.feature.bookshelf.selection)
    dokka(projects.feature.favorite)
    dokka(projects.feature.favorite.add)
    dokka(projects.feature.favorite.common)
    dokka(projects.feature.favorite.create)
    dokka(projects.feature.favorite.edit)
    dokka(projects.feature.file)
    dokka(projects.feature.folder)
    dokka(projects.feature.history)
    dokka(projects.feature.readlater)
    dokka(projects.feature.search)
    dokka(projects.feature.settings)
    dokka(projects.feature.settings.common)
    dokka(projects.feature.settings.display)
    dokka(projects.feature.settings.folder)
    dokka(projects.feature.settings.info)
    dokka(projects.feature.settings.security)
    dokka(projects.feature.settings.viewer)
    dokka(projects.feature.tutorial)
    dokka(projects.framework.common)
    dokka(projects.framework.designsystem)
    dokka(projects.framework.notification)
    dokka(projects.framework.ui)
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

tasks.register("reportMerge", io.gitlab.arturbosch.detekt.report.ReportMergeTask::class) {
    output.set(rootProject.layout.buildDirectory.file("reports/detekt/merge.sarif"))
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

version = "0.0.0-SNAPSHOT"
gitVersioning.apply {
    refs {
        tag("(?<version>.*)") {
            version = "\${describe.tag.version}"
        }
        branch("develop/.+") {
            version = "\${describe.tag.version}-\${describe.distance}-\${commit.short}-SNAPSHOT"
        }
    }
    rev {
        version = "\${commit}"
    }
}

tasks.updateDaemonJvm {
    jvmVersion = JavaLanguageVersion.of(libs.versions.java.get())
}

tasks.register("detektAll") {
    dependsOn(tasks.withType<Detekt>())
}
