plugins {
    `kotlin-dsl`
}

group = "com.sorrowblue.comicviewer.buildlogic"

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    implementation(libs.android.tools.build.gradle)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.arturbosch.detektGradlePlugin)
    implementation(libs.ksp.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("comicviewer.android.library") {
            id = name
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("comicviewer.android.library.compose") {
            id = name
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }

        register("comicviewer.android.application") {
            id = name
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("comicviewer.android.application.compose") {
            id = name
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }

        register("comicviewer.android.dynamic-feature") {
            id = name
            implementationClass = "AndroidDynamicFeatureConventionPlugin"
        }
        register("comicviewer.android.dynamic-feature.compose") {
            id = name
            implementationClass = "AndroidDynamicFeatureComposeConventionPlugin"
        }

        register("comicviewer.android.feature") {
            id = name
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("comicviewer.android.feature.dynamic-feature") {
            id = name
            implementationClass = "AndroidFeatureDynamicFeatureConventionPlugin"
        }
        register("comicviewer.android.hilt") {
            id = name
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("comicviewer.android.lint") {
            id = name
            implementationClass = "AndroidLintPlugin"
        }
        register("comicviewer.android.koin") {
            id = name
            implementationClass = "AndroidKoinConventionPlugin"
        }
    }
}
