plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.soil.form)
            }
        }
    }
}
compose.resources {
    publicResClass = true
}
android {
    namespace = "com.sorrowblue.comicviewer.feature.favorite.common"
    resourcePrefix("favorite_common")
}
