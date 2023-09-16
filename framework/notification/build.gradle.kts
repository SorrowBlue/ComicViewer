plugins {
    id("build-logic.android.library")
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.notification"

    resourcePrefix("framework_notification")
}

dependencies {
    implementation(projects.framework)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.startup.runtime)
}
