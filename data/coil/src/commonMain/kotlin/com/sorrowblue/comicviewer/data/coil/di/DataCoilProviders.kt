package com.sorrowblue.comicviewer.data.coil.di

import coil3.disk.DiskCache
import com.sorrowblue.comicviewer.data.coil.cache.CoilDiskCache
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import kotlinx.io.files.Path
import logcat.logcat

@ContributesTo(AppScope::class)
interface DataCoilProviders {
    @Provides
    private fun provideDiskCache(coilDiskCache: CoilDiskCache): DiskCache {
        val pathStr = coilDiskCache.resolve("image_cache").toString()
        logcat { "#provideDiskCache pathStr=$pathStr" }
        logcat { "#provideDiskCache kotlinx.io.files.Path=${Path(pathStr)}" }
        return DiskCache.Builder().directory(coilDiskCache.resolve("image_cache")).build().also {
            logcat { "#provideDiskCache okio.Path=${it.directory}" }
        }
    }
}

private fun DiskCache.Builder.directory(directory: Path) = directory.toString()
