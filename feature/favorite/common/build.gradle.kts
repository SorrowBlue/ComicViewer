plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.compose)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.framework.designsystem)
                implementation(projects.framework.ui)
                implementation(projects.domain.model)
                implementation(projects.domain.usecase)
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
