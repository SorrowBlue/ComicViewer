package comicviewer.convention

import com.sorrowblue.comicviewer.libs

plugins {
    id("comicviewer.convention.multiplatform-library")
    id("comicviewer.convention.multiplatform-compose")
    id("comicviewer.primitive.metro")
}

kotlin {
    androidLibrary {
        androidResources.enable = true
    }
    sourceSets.commonMain.dependencies {
        implementation(project(":framework:designsystem"))
        implementation(project(":framework:ui"))
        implementation(project(":domain:usecase"))

        // Image
        implementation(libs.coil3.compose)
        // Paging
        implementation(libs.androidx.pagingCommon)
    }
}
