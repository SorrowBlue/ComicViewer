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
                implementation(libs.aboutlibraries.composeM3) }
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

buildkonfig {
    packageName = "om.sorrowblue.comicviewer.feature.settings"
    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "VERSION_NAME", version.toString())
        buildConfigField(FieldSpec.Type.LONG, "TIMESTAMP", System.currentTimeMillis().toString())
    }
}
