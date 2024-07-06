package com.sorrowblue.comicviewer.data.coil.folder

import android.content.Context
import coil3.disk.DiskCache
import coil3.disk.directory
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

object CoilDiskCache {

    fun thumbnailDiskCache(context: Context, bookshelfId: BookshelfId) =
        diskCaches.getOrPut("thumbnail_cache_${bookshelfId.value}") {
            DiskCache.Builder().directory(
                context.cacheDir.resolve("thumbnail_cache_${bookshelfId.value}")
                    .apply { mkdirs() })
                .build()
        }

    fun pageDiskCache(context: Context, bookshelfId: BookshelfId) =
        diskCaches.getOrPut("page_cache_${bookshelfId.value}") {
            DiskCache.Builder().directory(
                context.cacheDir.resolve("page_cache_${bookshelfId.value}").apply { mkdirs() })
                .build()
        }

    @get:Synchronized
    private var diskCaches = mutableMapOf<String, DiskCache>()
}
