package com.sorrowblue.comicviewer.domain.model.settings.folder

import kotlinx.serialization.Serializable

/** Represents the filter quality for image thumbnails. */
@Serializable
enum class ImageFilterQuality {
    /** No filter quality. */
    None,

    /** Low filter quality. */
    Low,

    /** Medium filter quality. */
    Medium,

    /** High filter quality. */
    High,
}
