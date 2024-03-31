plugins {
    id("comicviewer.android.feature.dynamic-feature")
    id("comicviewer.android.koin")
    alias(libs.plugins.kotlin.plugin.serialization)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.library.box"
    resourcePrefix("box")
}

dependencies {
    implementation(projects.framework.notification)
    implementation(projects.domain.model)
    implementation(projects.feature.library)

    implementation(libs.androidx.browser)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.box.java.sdk) {
//        exclude("org.bouncycastle")
    }
    implementation(libs.kotlinx.serialization.protobuf)
}
