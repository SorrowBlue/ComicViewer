plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.feature.tutorial"
//        resourcePrefix("tutorial")
    }
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.google.android.play.feature.delivery.ktx)
            }
        }
    }
}
