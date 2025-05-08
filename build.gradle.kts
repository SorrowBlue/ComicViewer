import dev.iurysouza.modulegraph.ModuleType.Custom
import dev.iurysouza.modulegraph.Theme
import java.util.Locale

plugins {
    alias(libs.plugins.detekt)
    alias(libs.plugins.versions)
    alias(libs.plugins.licensee) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.modulegraph)
    alias(libs.plugins.kotlinx.kover)
    alias(libs.plugins.versionCatalogLinter)
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.dynamicFeature) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.androidx.room) apply false
    alias(libs.plugins.google.ksp) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.compose) apply false
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
    dokka(projects.domain.service)
    dokka(projects.domain.usecase)
    dokka(projects.feature.authentication)
    dokka(projects.feature.book)
    dokka(projects.feature.bookshelf)
    dokka(projects.feature.bookshelf.edit)
    dokka(projects.feature.bookshelf.info)
    dokka(projects.feature.bookshelf.selection)
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
    readmePath.set(layout.projectDirectory.file("README2.md").asFile.path)
    rootModulesRegex.set("^(:composeApp).*")
    nestingEnabled.set(true)
    setStyleByModuleType.set(true)
    excludedModulesRegex.set(".*(framework|aggregate|di|data|domain|folder|settings:|file).*")
    theme.set(
        Theme.BASE(
            moduleTypes = listOf(
                Custom(id = "comicviewer.kotlinMultiplatform.application", color = "#2962FF"),
                Custom(id = "comicviewer.kotlinMultiplatform.dynamicfeature", color = "#FF6D00"),
                Custom(id = "comicviewer.kotlinMultiplatform.library", color = "#00C853"),
            )
        )
    )
    graph(layout.projectDirectory.file("README2.md").asFile.path, "## Data") {
        rootModulesRegex = "^:data(?!:di\$).+"
        nestingEnabled = true
        setStyleByModuleType = true
        strictMode = true
        excludedModulesRegex = ".*(framework|feature|aggregate|composeApp|di).*"
        theme = Theme.BASE(
            moduleTypes = listOf(
                Custom(id = "comicviewer.kotlinMultiplatform.application", color = "#2962FF"),
                Custom(id = "comicviewer.kotlinMultiplatform.dynamicfeature", color = "#FF6D00"),
                Custom(id = "comicviewer.kotlinMultiplatform.library", color = "#00C853"),
            )
        )
    }
}

tasks.updateDaemonJvm {
    languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
}
