plugins {
    alias(libs.plugins.comicviewer.android.library)
}

android {
    namespace = "com.sorrowblue.comicviewer.data.reader"
}

dependencies {
    api(libs.squareup.okio)
}
