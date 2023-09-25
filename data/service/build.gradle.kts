plugins {
    id("comicviewer.android.library")
    id("comicviewer.android.hilt")
}

android {
    namespace = "com.sorrowblue.comicviewer.data.service"
}

dependencies {
    implementation(projects.framework.notification)
    implementation(projects.data.infrastructure)

    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.androidx.work.runtime.ktx)
}
