plugins {
    id("com.android.library")
id("org.jetbrains.kotlin.android")
id("build-logic.android.library")

    id("org.jetbrains.kotlin.kapt")
    id("dagger.hilt.android.plugin")
}

android {
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    api(libs.androidx.activity)
    api(libs.androidx.constraintlayout)
    api(libs.androidx.coordinatorlayout)
    api(libs.androidx.core.ktx)
    api(libs.androidx.fragment.ktx)
    api(libs.bundles.androidx.lifecycle)
    api(libs.bundles.androidx.navigation)
    api(libs.androidx.recyclerview)
    api(libs.androidx.swiperefreshlayout)

    implementation(libs.androidx.preference.ktx)

    api(libs.coil)
    api(libs.google.material)
    api(libs.kotlinx.coroutines.core)
    api(libs.squareup.logcat)
    api(libs.chrisbanes.insetter)
    api(libs.sorrowblue.binding.ktx)

    implementation(libs.dagger.hilt.android.core)
    kapt(libs.dagger.hilt.android.compiler)
}
