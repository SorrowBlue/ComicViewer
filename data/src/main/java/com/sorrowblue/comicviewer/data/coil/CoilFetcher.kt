package com.sorrowblue.comicviewer.data.coil

import coil.annotation.ExperimentalCoilApi
import coil.decode.ImageSource
import coil.disk.DiskCache
import coil.fetch.Fetcher
import coil.request.Options

@OptIn(ExperimentalCoilApi::class)
abstract class CoilFetcher<T>(
    private val data: T,
    private val options: Options,
    diskCacheLazy: dagger.Lazy<DiskCache?>,
) : Fetcher {

    abstract val diskCacheKey: String

    protected val diskCache by diskCacheLazy
    protected val fileSystem get() = diskCache!!.fileSystem

    protected fun readFromDiskCache(): DiskCache.Snapshot? {
        return if (options.diskCachePolicy.readEnabled) {
            diskCache?.get(diskCacheKey)
        } else {
            null
        }
    }

    protected fun DiskCache.Snapshot.toImageSource(): ImageSource {
        return ImageSource(data, fileSystem, diskCacheKey, this)
    }
}
