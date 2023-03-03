package com.sorrowblue.comicviewer.data.coil.book

import android.graphics.Bitmap
import android.os.Build
import coil.annotation.ExperimentalCoilApi
import coil.decode.ImageSource
import coil.disk.DiskCache
import coil.fetch.Fetcher
import coil.request.Options
import coil.size.pxOrElse
import com.sorrowblue.comicviewer.data.coil.getValue

@OptIn(ExperimentalCoilApi::class)
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
            diskCache?.get(diskCacheKey)
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
        val COMPRESS_FORMAT =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Bitmap.CompressFormat.WEBP_LOSSY else Bitmap.CompressFormat.JPEG
    }
}
