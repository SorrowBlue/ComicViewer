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
                implementation(libs.androidx.pagingCommon)
                implementation(libs.androidx.pagingCompose)
                implementation(libs.coil3.compose)
                implementation(libs.kotlinx.collectionsImmutable)
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
                implementation(libs.kotlinx.coroutinesSwing)
            }
        }
    }
}
