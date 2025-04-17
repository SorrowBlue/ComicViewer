package com.sorrowblue.comicviewer.domain.model.settings.folder

import kotlinx.serialization.Serializable

@Serializable
enum class FolderThumbnailOrder { NAME, MODIFIED, LAST_READ }
