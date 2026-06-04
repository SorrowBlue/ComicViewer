package com.sorrowblue.comicviewer.data.coil.fetcher

import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.disk.DiskCache
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import com.sorrowblue.comicviewer.data.coil.abortQuietly
import com.sorrowblue.comicviewer.data.coil.closeQuietly
import kotlinx.io.IOException
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.FileSystem
import kotlinx.io.files.SystemFileSystem

internal abstract class BaseFetcher<T : Any, V : CoilMetadata>(
    private val data: T,
    protected val options: Options,
    private val diskCache: Lazy<DiskCache?>,
) : Fetcher {

    override suspend fun fetch(): FetchResult? = doFetch()

    protected suspend fun fastPath(snapshot: DiskCache.Snapshot?): SourceFetchResult? {
        // Fast path: fetch the image from the disk cache without performing a network request.
        if (snapshot != null) {
            // Always return files with empty metadata as it's likely they've been written
            // to the disk cache manually.
            if (fileSystem.metadataOrNull(snapshot.metadata.asKotlinxPath())?.size == 0L) {
                return SourceFetchResult(
                    source = snapshot.toImageSource(),
                    mimeType = "image/*",
                    dataSource = DataSource.DISK,
                )
            }

            // Return the image from the disk cache if the cache strategy agrees.
            if (snapshot.toMetadataOrNull() == metadata()) {
                return SourceFetchResult(
                    source = snapshot.toImageSource(),
                    mimeType = "image/*",
                    dataSource = DataSource.DISK,
                )
            }
        }
        return null
    }

    protected abstract suspend fun doFetch(): FetchResult

    protected fun readFromDiskCache(): DiskCache.Snapshot? {
        if (options.diskCachePolicy.readEnabled) {
            return diskCache.value?.openSnapshot(diskCacheKey)
        } else {
            return null
        }
    }

    protected suspend fun writeToDiskCache(
        snapshot: DiskCache.Snapshot?,
        metaData: V,
        writeTo: suspend (Sink) -> Unit,
    ): DiskCache.Snapshot? {
        // Short circuit if we're not allowed to cache this response.
        if (!options.diskCachePolicy.writeEnabled) {
            snapshot?.closeQuietly()
            return null
        }

        // Open a new editor. Return null if we're unable to write to this entry.
        val editor = if (snapshot != null) {
            snapshot.closeAndOpenEditor()
        } else {
            diskCache.value?.openEditor(diskCacheKey)
        } ?: return null

        // Write the network request metadata and the network response body to disk.
        runCatching {
            fileSystem.sink(editor.metadata.asKotlinxPath()).buffered().use {
                metaData.writeTo(it)
            }
            fileSystem.sink(editor.data.asKotlinxPath()).buffered().use {
                writeTo(it)
            }
            return editor.commitAndOpenSnapshot()
        }.onFailure {
            editor.abortQuietly()
        }.getOrThrow()
    }

    abstract suspend fun metadata(): V

    protected abstract fun readFrom(source: Source): V

    private fun DiskCache.Snapshot.toMetadataOrNull(): V? = try {
        fileSystem.source(metadata.asKotlinxPath()).buffered().use {
            readFrom(it)
        }
    } catch (_: IOException) {
        // If we can't parse the metadata, ignore this entry.
        null
    }

    protected fun DiskCache.Snapshot.toImageSource(): ImageSource = ImageSource(
        file = data.asKotlinxPath(),
        fileSystem = fileSystem,
        diskCacheKey = diskCacheKey,
        closeable = this,
    )

    protected fun Source.toImageSource(): ImageSource = ImageSource(
        source = this,
        fileSystem = fileSystem,
    )

    abstract val diskCacheKey: String

    private val fileSystem: FileSystem
        get() = SystemFileSystem
}
