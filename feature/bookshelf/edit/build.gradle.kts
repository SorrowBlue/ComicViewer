plugins {
    id("comicviewer.android.feature")
    id("comicviewer.android.library.test")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.bookshelf.edit"
    resourcePrefix("bookshelf_edit")

    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
}
