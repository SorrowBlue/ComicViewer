plugins {
    id("comicviewer.android.library")
}

android {
    namespace = "com.sorrowblue.comicviewer.data.common"
}

dependencies {
    implementation(projects.domain.common)
}
