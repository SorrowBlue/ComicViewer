import com.sorrowblue.comicviewer.GitTagValueSource

plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
    alias(libs.plugins.buildconfig)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.feature.settings.info"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.settings.common)
                implementation(projects.feature.settings.nav)
                implementation(projects.feature.tutorial.nav)
                implementation(libs.kotlinx.datetime)
                implementation(libs.aboutlibraries.compose)
            }
        }
    }
}

val gitTagProvider: Provider<String>
    get() = providers.of(GitTagValueSource::class) {}

val gitCommitTimestampProvider: Provider<Long>
    get() = providers.exec {
        commandLine("git", "log", "-1", "--format=%ct")
        isIgnoreExitValue = true
    }.standardOutput.asText.map { output ->
        output.trim().toLongOrNull()?.times(1000L) ?: 0L
    }

buildConfig {
    packageName = "om.sorrowblue.comicviewer.feature.settings"
    buildConfigField("VERSION_NAME", gitTagProvider)
    buildConfigField("TIMESTAMP", gitCommitTimestampProvider)
}
