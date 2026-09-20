/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.extension.subscreen.imagecache

import com.sorrowblue.comicviewer.domain.model.cache.BookshelfImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.cache.OtherImageCache
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

internal data class ThumbnailScreenUiState(
    val imageCacheInfos: PersistentList<BookshelfImageCacheInfo> = persistentListOf(),
    val otherImageCache: OtherImageCache? = null,
)
