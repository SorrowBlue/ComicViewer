plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.android.tools.build.gradle)
    implementation(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("android.application") {
            id = "build-logic.android.application"
            implementationClass = "com.sorrowblue.buildlogic.AndroidApplicationPlugin"
        }
        register("android.library") {
            id = "build-logic.android.library"
            implementationClass = "com.sorrowblue.buildlogic.AndroidLibraryPlugin"
        }
        register("android.dynamic-feature") {
            id = "build-logic.android.dynamic-feature"
            implementationClass = "com.sorrowblue.buildlogic.AndroidDynamicFeaturePlugin"
        }
        register("com.sorrowblue.dagger-hilt") {
            id = "com.sorrowblue.dagger-hilt"
            implementationClass = "com.sorrowblue.buildlogic.AndroidDaggerHiltPlugin"
        }
        register("com.sorrowblue.android-feature") {
            id = name
            implementationClass = "com.sorrowblue.buildlogic.ComicViewerAndroidFeaturePlugin"
        }
    }
}
