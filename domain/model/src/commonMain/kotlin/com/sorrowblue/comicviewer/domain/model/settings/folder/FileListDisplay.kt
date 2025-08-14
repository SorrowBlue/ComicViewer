package com.sorrowblue.comicviewer.domain.model.settings.folder

import kotlinx.serialization.Serializable

/** Represents the display type for file and folder lists. */
@Serializable
enum class FileListDisplay {
    /** Display items in a grid layout. */
    Grid,

    /** Display items in a list layout. */
    List,
}
