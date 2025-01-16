package com.sorrowblue.comicviewer.data.coil.di

import coil3.disk.DiskCache
import com.sorrowblue.comicviewer.data.coil.CoilDiskCache
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
@ComponentScan("com.sorrowblue.comicviewer.data.coil")
class DataCoilModule {

    @Singleton
    fun provideImageCacheDiskCacheDiskCache(coilDiskCache: CoilDiskCache): DiskCache {
        return DiskCache.Builder().directory(coilDiskCache.resolve("image_cache")).build()
    }
}
