plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

tasks.withType<Test>().configureEach {
    if (name.contains("AndroidHostTest")) {
        filter {
            isFailOnNoMatchingTests = false
            excludeTestsMatching("*UiTest")
        }
    }
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.feature.bookshelf.edit"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.bookshelf.nav)
                implementation(projects.feature.permission.nav)
                implementation(projects.framework.permission)
                implementation(libs.filekit.compose)
            }
        }
        commonTest {
            dependencies {
                implementation(projects.framework.test)
                implementation(libs.compose.uiTest)
            }
        }
        jvmTest {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutinesSwing)
            }
        }
    }
}
