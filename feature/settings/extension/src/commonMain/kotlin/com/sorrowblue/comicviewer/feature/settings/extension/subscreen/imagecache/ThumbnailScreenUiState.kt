/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.extension.subscreen.imagecache

import com.sorrowblue.comicviewer.domain.model.cache.BookshelfImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.cache.OtherImageCache

internal data class ThumbnailScreenUiState(
    val imageCacheInfos: List<BookshelfImageCacheInfo> = emptyList(),
    val otherImageCache: OtherImageCache? = null,
)
