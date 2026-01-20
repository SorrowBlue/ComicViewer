import com.codingfeline.buildkonfig.compiler.FieldSpec

plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
    alias(libs.plugins.buildkonfig)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.feature.settings.info"
    }
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

val gitTagProvider = providers.of(GitTagValueSource::class) {}

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
