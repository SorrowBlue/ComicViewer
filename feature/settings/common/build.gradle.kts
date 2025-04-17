plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.settings.common"
    resourcePrefix("settings_common")
}
