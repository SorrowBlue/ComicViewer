package com.sorrowblue.comicviewer.library

sealed interface Library {
    val viewType: LibraryListViewType
}

enum class CloudStorage : Library {
    GOOGLE_DRIVE,
    ONE_DRIVE,
    BOX,
    MEGA,
    DROP_BOX;

    override val viewType = LibraryListViewType.CLOUD_STORAGE
}

enum class LocalFeature : Library {
    DOWNLOADED;

    override val viewType = LibraryListViewType.LOCAL_FEATURE
}
