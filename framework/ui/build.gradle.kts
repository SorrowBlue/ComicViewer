plugins {
    id("com.android.library")
id("org.jetbrains.kotlin.android")
id("build-logic.android.library")

    id("org.jetbrains.kotlin.kapt")
}

android {
    resourcePrefix = "framework_ui"
    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    api(projects.framework.resource)


    api(libs.androidx.constraintlayout)
    api(libs.androidx.coordinatorlayout)
    api(libs.androidx.core.ktx)
    api(libs.androidx.fragment.ktx)
    api(libs.bundles.androidx.lifecycle)
    api(libs.bundles.androidx.navigation)
    api(libs.androidx.recyclerview)
    api(libs.androidx.swiperefreshlayout)
    api(libs.androidx.viewpager2)

    api(libs.androidx.biometric)

    api(libs.google.material)
    api(libs.coil)
    api(libs.kotlinx.coroutines.core)
    api(libs.squareup.logcat)
    api(libs.chrisbanes.insetter)
    api(libs.sorrowblue.binding.ktx)

}
