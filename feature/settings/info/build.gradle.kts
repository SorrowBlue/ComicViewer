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

buildConfig {
    packageName = "om.sorrowblue.comicviewer.feature.settings"
    buildConfigField(
        "VERSION_NAME",
        gitTagProvider.orElse("unknown").get(),
    )
    buildConfigField(
        "TIMESTAMP",
        System.currentTimeMillis(),
    )
}
