package com.sorrowblue.comicviewer.data.coil

import coil3.disk.DiskCache
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import kotlinx.atomicfu.atomic
import okio.Path

expect class CoilDiskCache {
    fun resolve(folder: String): Path
}

internal fun CoilDiskCache.thumbnailDiskCache(bookshelfId: BookshelfId): DiskCache {
    return diskCaches.value.getOrPut("thumbnail_cache_${bookshelfId.value}") {
        DiskCache.Builder()
            .directory(resolve("thumbnail_cache_${bookshelfId.value}"))
            .build()
    }
}

internal fun CoilDiskCache.pageDiskCache(bookshelfId: BookshelfId): DiskCache {
    return diskCaches.value.getOrPut("page_cache_${bookshelfId.value}") {
        DiskCache.Builder()
            .directory(resolve("page_cache_${bookshelfId.value}"))
            .build()
    }
}

private val diskCaches = atomic(mutableMapOf<String, DiskCache>())
