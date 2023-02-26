package com.sorrowblue.comicviewer.library

enum class LocalFeature : Library {
    DOWNLOADED;

    override val viewType = LibraryListViewType.LOCAL_FEATURE
}
