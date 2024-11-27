plugins {
    alias(libs.plugins.comicviewer.android.feature)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.bookshelf.info"
    resourcePrefix("bookshelf_info")
}

dependencies {
    implementation(projects.framework.notification)
    implementation(projects.feature.file)

    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.work.runtime)
    ksp(libs.androidx.hilt.compiler)
}
