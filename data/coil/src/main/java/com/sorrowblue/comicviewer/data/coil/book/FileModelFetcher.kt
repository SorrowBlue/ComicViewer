package com.sorrowblue.comicviewer.data.coil.book

import android.graphics.Bitmap
import coil3.decode.ImageSource
import coil3.disk.DiskCache
import coil3.fetch.Fetcher
import coil3.request.Options
import coil3.size.pxOrElse
import com.sorrowblue.comicviewer.data.coil.getValue

internal abstract class FileModelFetcher(
    val options: Options,
    diskCacheLazy: dagger.Lazy<DiskCache?>,
) : Fetcher {
    protected val diskCache by diskCacheLazy

    protected val requestWidth = options.size.width.pxOrElse { 300 }.toFloat()
    protected val requestHeight = options.size.height.pxOrElse { 300 }.toFloat()
    abstract val diskCacheKey: String

    protected fun readFromDiskCache(): DiskCache.Snapshot? {
        return if (options.diskCachePolicy.readEnabled) {
            diskCache?.openSnapshot(diskCacheKey)
        } else {
            null
        }
    }

    protected fun isCacheable(): Boolean {
        return options.diskCachePolicy.writeEnabled
    }

    protected fun DiskCache.Snapshot.toImageSource(): ImageSource {
        return ImageSource(data, fileSystem, diskCacheKey, this)
    }

    protected val fileSystem get() = diskCache!!.fileSystem

    companion object {
        val COMPRESS_FORMAT = Bitmap.CompressFormat.WEBP_LOSSY
    }
}
