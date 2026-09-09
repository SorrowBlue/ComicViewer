import dev.detekt.gradle.report.ReportMergeTask
import nl.littlerobots.vcu.plugin.resolver.VersionSelectors

plugins {
    alias(libs.plugins.dependencyAnalysis)
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.androidMultiplatform) apply false
    alias(libs.plugins.androidTest) apply false
    alias(libs.plugins.androidxRoom3) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.aboutlibraries) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.metro) apply false
    alias(libs.plugins.buildconfig) apply false
    alias(libs.plugins.navgraph) apply false
    alias(libs.plugins.versionCatalogLinter)
    alias(libs.plugins.dokka)
    alias(libs.plugins.detekt)
    id("nl.littlerobots.version-catalog-update") version "1.1.1"
}

dependencyAnalysis {
    issues {
        all {
            onAny {
                exclude("dev.zacsweers.metro:runtime")
                exclude("org.jetbrains.compose.hot-reload:hot-reload-runtime-api")
                excludeRegex("org\\.jetbrains\\.compose\\.desktop:desktop.*")
                exclude(":framework:common")
            }
        }
    }
    structure {
        ignoreKtx(true)
        bundle("coil3") {
            includeGroup("io.coil-kt.coil3")
        }
        bundle("kotlinx-serialization") {
            include("org\\.jetbrains\\.kotlinx:kotlinx-serialization.*")
        }
        bundle("kotlinx-coroutines") {
            include("org\\.jetbrains\\.kotlinx:kotlinx-coroutines.*")
        }
        bundle("androidx.benchmark-macro") {
            include("androidx\\.benchmark:benchmark-macro.*")
        }
        bundle("androidx.sqlite") {
            includeGroup("androidx.sqlite")
        }
        bundle("androidx.room3") {
            includeGroup("androidx.room3")
        }
        bundle("androidx.paging") {
            includeGroup("androidx.paging")
        }
        bundle("org.jetbrains.compose.desktop") {
            includeGroup("org.jetbrains.compose.desktop")
        }
        bundle("org.jetbrains.compose.ui") {
            includeGroup("org.jetbrains.compose.ui")
        }
        bundle("androidx.compose.runtime") {
            includeGroup("androidx.compose.runtime")
        }
        bundle("org.jetbrains.compose.components:components-animatedimage") {
            include("org\\.jetbrains\\.compose\\.components:components-animatedimage.*")
        }
    }
    abi {
        exclusions {
            ignoreInternalPackages()
            ignoreGeneratedCode()
            excludeClasses("kotlinx\\.serialization.*")
            excludeAnnotations("kotlinx\\.serialization.*")
            excludeClasses(""".+\$\$${"serializer"}$""")
            excludeClasses(""".+\$${"Companion"}$""")
        }
    }
}

tasks.named<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

konture {
    excludeConfigurations("dokka", "kover")
    excludeModules(":framework:common", ":")
}

versionCatalogUpdate {
    versionSelector(VersionSelectors.LATEST)
}
dependencies {
    dokka(projects.app.androidApp)
    dokka(projects.app.ios)
    dokka(projects.app.jvmApp)
    dokka(projects.app.share)
    dokka(projects.data.coil)
    dokka(projects.data.database)
    dokka(projects.data.datastore)
    dokka(projects.data.reader.document)
    dokka(projects.data.reader.zip)
    dokka(projects.data.storage)
    dokka(projects.data.storage.device)
    dokka(projects.data.storage.smb)
    dokka(projects.domain.model)
    dokka(projects.domain.repository)
    dokka(projects.domain.service)
    dokka(projects.domain.usecase)
    dokka(projects.feature.authentication)
    dokka(projects.feature.book)
    dokka(projects.feature.book.nav)
    dokka(projects.feature.bookshelf)
    dokka(projects.feature.bookshelf.edit)
    dokka(projects.feature.bookshelf.info)
    dokka(projects.feature.collection)
    dokka(projects.feature.collection.add)
    dokka(projects.feature.collection.editor)
    dokka(projects.feature.collection.nav)
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
    dokka(projects.feature.settings.nav)
    dokka(projects.feature.settings.extension)
    dokka(projects.feature.settings.security)
    dokka(projects.feature.settings.viewer)
    dokka(projects.feature.tutorial)
    dokka(projects.framework.common)
    dokka(projects.framework.designsystem)
    dokka(projects.framework.notification)
    dokka(projects.framework.startup)
    dokka(projects.framework.test)
    dokka(projects.framework.ui)
}

val reportMerge = tasks.register("reportMerge", ReportMergeTask::class) {
    description = "Merge all detekt reports into a single report"
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    output.set(rootProject.layout.buildDirectory.file("reports/detekt/merge.sarif"))
}

tasks.updateDaemonJvm {
    vendor = JvmVendorSpec.ADOPTIUM
    languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
}
