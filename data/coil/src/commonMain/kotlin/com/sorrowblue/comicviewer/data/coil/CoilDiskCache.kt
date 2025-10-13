package com.sorrowblue.comicviewer.data.coil

import coil3.disk.DiskCache
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import okio.Path
import jakarta.inject.Singleton

@Singleton
expect class CoilDiskCache(context: PlatformContext) {
    fun resolve(folder: String): Path
}

@OptIn(ExperimentalAtomicApi::class)
internal fun CoilDiskCache.thumbnailDiskCache(bookshelfId: BookshelfId): DiskCache {
    return diskCaches.load().getOrPut("thumbnail_cache_${bookshelfId.value}") {
        DiskCache.Builder()
            .directory(resolve("thumbnail_cache_${bookshelfId.value}"))
            .build()
    }
}

@OptIn(ExperimentalAtomicApi::class)
internal fun CoilDiskCache.pageDiskCache(bookshelfId: BookshelfId): DiskCache {
    return diskCaches.load().getOrPut("page_cache_${bookshelfId.value}") {
        DiskCache.Builder()
            .directory(resolve("page_cache_${bookshelfId.value}"))
            .build()
    }
}

@OptIn(ExperimentalAtomicApi::class)
private val diskCaches = AtomicReference(mutableMapOf<String, DiskCache>())
