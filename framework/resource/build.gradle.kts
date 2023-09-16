plugins {
    id("build-logic.android.library")
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.resource"
}

dependencies {
    implementation(libs.google.material)
}
