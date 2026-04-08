package com.sorrowblue.comicviewer.data.coil.di

import coil3.disk.DiskCache
import com.sorrowblue.comicviewer.data.coil.cache.CoilDiskCache
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(DataScope::class)
interface DataCoilProviders {
    @Provides
    private fun provideDiskCache(coilDiskCache: CoilDiskCache): DiskCache =
        DiskCache.Builder().directory(coilDiskCache.resolve("image_cache")).build()
}
