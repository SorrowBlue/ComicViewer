plugins {
    alias(libs.plugins.comicviewer.android.library)
}

android {
    namespace = "com.sorrowblue.comicviewer.data.reader"
}

dependencies {
    api("com.squareup.okio:okio:3.9.0")
}
