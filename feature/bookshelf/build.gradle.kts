plugins {
    alias(libs.plugins.comicviewer.android.feature)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.bookshelf"
    resourcePrefix("bookshelf")
}

dependencies {
    implementation(projects.framework.notification)
    implementation(projects.feature.bookshelf.edit)
    implementation(projects.feature.bookshelf.remove)
    implementation(projects.feature.bookshelf.selection)
    implementation(projects.feature.folder)

    implementation(libs.androidx.work.runtime)

    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    implementation(libs.jcifs.ng)
}
