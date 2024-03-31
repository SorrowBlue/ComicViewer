plugins {
    id("comicviewer.android.feature")
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.bookshelf.edit"
    resourcePrefix("bookshelf_edit")

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    testImplementation(libs.androidx.test.ext.junitKtx)
    testImplementation(libs.androidx.test.ext.truth)
    testImplementation(libs.androidx.compose.ui.testJunit4)
    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
}
