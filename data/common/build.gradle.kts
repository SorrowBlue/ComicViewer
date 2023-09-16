plugins {
    id("build-logic.android.library")
}

android {
    namespace = "com.sorrowblue.comicviewer.data.common"
}

dependencies {
    implementation(projects.domain.common)
}
