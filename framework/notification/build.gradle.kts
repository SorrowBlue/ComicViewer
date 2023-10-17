plugins {
    id("comicviewer.android.library")
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.notification"

    resourcePrefix("framework_notification")
}

dependencies {
    implementation(projects.framework.common)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.startup.runtime)
}
