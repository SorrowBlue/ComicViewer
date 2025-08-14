package com.sorrowblue.comicviewer.domain.model.settings.folder

import kotlinx.serialization.Serializable

/** Represents the scale type for image thumbnails. */
@Serializable
enum class ImageScale {
    /** Crop the thumbnail image. */
    Crop,

    /** Fit the thumbnail image to the container. */
    Fit,
}
