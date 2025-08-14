package com.sorrowblue.comicviewer.domain.model.settings.folder

import kotlinx.serialization.Serializable

/** Represents the grid column size for displaying items. */
@Serializable
enum class GridColumnSize {
    /** Medium grid column size. */
    Medium,

    /** Large grid column size. */
    Large,
}
