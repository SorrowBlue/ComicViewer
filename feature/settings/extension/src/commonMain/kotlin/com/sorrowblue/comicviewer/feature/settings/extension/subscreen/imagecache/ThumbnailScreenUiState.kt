package com.sorrowblue.comicviewer.feature.settings.extension.subscreen.imagecache

import com.sorrowblue.comicviewer.domain.model.BookshelfImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.OtherImageCache

internal data class ThumbnailScreenUiState(
    val imageCacheInfos: List<BookshelfImageCacheInfo> = emptyList(),
    val otherImageCache: OtherImageCache? = null,
)
