package com.sorrowblue.comicviewer.domain.model

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf

sealed interface ImageCacheInfo {
    val size: Long
    val maxSize: Long
}

data class BookshelfImageCacheInfo(
    val bookshelf: Bookshelf,
    val type: Type,
    override val size: Long,
    override val maxSize: Long,
) : ImageCacheInfo {

    enum class Type {
        Thumbnail,
        Page,
    }
}

data class FavoriteImageCacheInfo(
    override val size: Long,
    override val maxSize: Long,
) : ImageCacheInfo
