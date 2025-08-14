package com.sorrowblue.comicviewer.domain.model.settings.folder

import kotlinx.serialization.Serializable

/** Represents the order type for folder thumbnails. */
@Serializable
enum class FolderThumbnailOrder {
    /** Order by name. */
    NAME,

    /** Order by modified date. */
    MODIFIED,

    /** Order by last read date. */
    LAST_READ,
}
