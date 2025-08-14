package com.sorrowblue.comicviewer.domain.model.settings.folder

import kotlinx.serialization.Serializable

/** Represents the image format for thumbnails. */
@Serializable
enum class ImageFormat {
    /** WEBP image format. */
    WEBP,

    /** JPEG image format. */
    JPEG,

    /** PNG image format. */
    PNG,
}
