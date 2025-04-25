import com.codingfeline.buildkonfig.compiler.FieldSpec

plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
    alias(libs.plugins.buildkonfig)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.settings.common)
                implementation(libs.kotlinx.datetime)
                implementation(libs.aboutlibraries.compose)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.browser)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.settings.info"
    resourcePrefix("settings_info")
}

val gitTagProvider: Provider<String> = providers.of(GitTagValueSource::class) {}

buildkonfig {
    packageName = "om.sorrowblue.comicviewer.feature.settings"
    defaultConfigs {
        buildConfigField(
            type = FieldSpec.Type.STRING,
            name = "VERSION_NAME",
            value = gitTagProvider.orElse("unknown").get(),
            const = true
        )
        buildConfigField(
            FieldSpec.Type.LONG,
            "TIMESTAMP",
            System.currentTimeMillis().toString(),
            const = true
        )
    }
}
