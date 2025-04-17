package com.sorrowblue.comicviewer.domain.model.settings.folder

import kotlinx.serialization.Serializable

/** サムネイルフィルターの品質 */
@Serializable
enum class ImageFilterQuality { None, Low, Medium, High }
