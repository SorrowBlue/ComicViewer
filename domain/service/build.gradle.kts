plugins {
    id("comicviewer.android.library")
    id("comicviewer.android.hilt")
}

android {
    namespace = "com.sorrowblue.comicviewer.domain.service"
}

dependencies {
    api(projects.domain.model)
    implementation(projects.domain.usecase)
    implementation(projects.domain.reader)

    implementation(libs.androidx.paging.common)
    implementation(libs.google.android.play.feature.delivery.ktx)
}
