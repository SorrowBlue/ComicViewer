package com.sorrowblue.comicviewer.data.coil.di

import coil3.disk.DiskCache
import com.sorrowblue.comicviewer.data.coil.CoilDiskCache
import jakarta.inject.Singleton

@Singleton
internal fun provideImageCacheDiskCacheDiskCache(coilDiskCache: CoilDiskCache): DiskCache {
    return DiskCache.Builder().directory(coilDiskCache.resolve("image_cache")).build()
}
