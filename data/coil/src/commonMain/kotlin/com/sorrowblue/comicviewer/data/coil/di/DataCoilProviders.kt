/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.coil.di

import coil3.disk.DiskCache
import com.sorrowblue.comicviewer.data.coil.cache.CoilDiskCache
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import logcat.logcat

@ContributesTo(AppScope::class)
@BindingContainer
object DataCoilProviders {
    @Provides
    private fun CoilDiskCache.provideDiskCache(): DiskCache =
        DiskCache.Builder().directory(resolve("image_cache")).build().also {
            logcat { "#provideDiskCache okio.Path=${it.directory}" }
        }
}
