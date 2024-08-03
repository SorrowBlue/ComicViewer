plugins {
    alias(libs.plugins.comicviewer.android.feature)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.favorite.common"
    resourcePrefix("favorite_common")
}

dependencies {
    api("com.soil-kt.soil:form:1.0.0-alpha04")
}
