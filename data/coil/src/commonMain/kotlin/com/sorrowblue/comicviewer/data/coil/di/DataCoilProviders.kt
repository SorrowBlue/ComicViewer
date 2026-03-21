package com.sorrowblue.comicviewer.data.coil.di

import coil3.disk.DiskCache
import com.sorrowblue.comicviewer.data.coil.CoilDiskCache
import com.sorrowblue.comicviewer.data.coil.impl.ImageCacheDataSourceImpl
import com.sorrowblue.comicviewer.data.coil.impl.ThumbnailDataSourceImpl
import com.sorrowblue.comicviewer.domain.service.datasource.ImageCacheDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.ThumbnailDataSource
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(DataScope::class)
interface DataCoilProviders {
    @Provides
    private fun provideDiskCache(coilDiskCache: CoilDiskCache): DiskCache =
        DiskCache.Builder().directory(coilDiskCache.resolve("image_cache")).build()

    @Binds
    private val ImageCacheDataSourceImpl.bind: ImageCacheDataSource get() = this

    @Binds
    private val ThumbnailDataSourceImpl.bind: ThumbnailDataSource get() = this
}
