/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.feature.folder.nav"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain.model)
        }
    }
}
