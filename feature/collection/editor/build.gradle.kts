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
        namespace = "com.sorrowblue.comicviewer.feature.collection.editor"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.collection.nav)
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
