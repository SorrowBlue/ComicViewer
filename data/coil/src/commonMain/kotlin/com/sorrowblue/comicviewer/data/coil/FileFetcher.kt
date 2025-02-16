package com.sorrowblue.comicviewer.data.coil

import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.disk.DiskCache
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import coil3.size.pxOrElse
import okio.BufferedSink
import okio.BufferedSource
import okio.Sink
import okio.buffer

/**
 * File fetcher
 *
 * @param T メタデータ
 * @param diskCacheLazy
 * @constructor
 * @property options
 */
internal abstract class FileFetcher<T : CoilMetadata>(
    val options: Options,
    private val diskCacheLazy: Lazy<DiskCache>,
) : Fetcher {

    /** Disk cache key */
    abstract val diskCacheKey: String

    /**
     * Metadata
     *
     * @return
     */
    abstract suspend fun metadata(): T

    /**
     * BufferedSourceからメタデータを読み取る
     *
     * @return メタデータ
     */
    abstract fun BufferedSource.readMetadata(): T?

    /**
     * Inner fetch
     *
     * @param snapshot
     * @return
     */
    abstract suspend fun innerFetch(snapshot: DiskCache.Snapshot?): FetchResult?

    override suspend fun fetch(): FetchResult? {
        val snapshot = readFromDiskCache()
        try {
            if (snapshot != null) {
                // Always return files with empty metadata as it's likely they've been written
                // to the disk cache manually.
                if (fileSystem.metadata(snapshot.metadata).size == 0L) {
                    return SourceFetchResult(
                        source = snapshot.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.DISK
                    )
                }

                // Return the image from the disk cache if the cache strategy agrees.
                if (snapshot.readMetadata() == metadata()) {
                    return SourceFetchResult(
                        source = snapshot.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.DISK
                    )
                }
            }

            // Slow path: fetch the image from the network.
            return innerFetch(snapshot)
        } catch (e: Exception) {
            snapshot?.closeQuietly()
            throw e
        }
    }

    protected val requestWidth = options.size.width.pxOrElse { 300 }.toFloat()
    protected val requestHeight = options.size.height.pxOrElse { 300 }.toFloat()

    protected suspend fun writeToDiskCache(
        snapshot: DiskCache.Snapshot?,
        metaData: T,
        writeTo: suspend (BufferedSink) -> Unit,
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
            diskCacheLazy.value.openEditor(diskCacheKey)
        } ?: return null

        // Write the network request metadata and the network response body to disk.
        try {
            fileSystem.write(editor.metadata) {
                metaData.writeTo(this)
            }
            fileSystem.write(editor.data) {
                writeTo(this)
            }
            return editor.commitAndOpenSnapshot()
        } catch (e: Exception) {
            editor.abortQuietly()
            throw e
        }
    }

    protected fun DiskCache.Snapshot.toImageSource() =
        ImageSource(
            file = data,
            fileSystem = fileSystem,
            diskCacheKey = diskCacheKey,
            closeable = this
        )

    protected fun Sink.toImageSource() =
        ImageSource(source = buffer().buffer, fileSystem = options.fileSystem)

    private val fileSystem get() = diskCacheLazy.value.fileSystem

    private fun readFromDiskCache(): DiskCache.Snapshot? {
        return if (options.diskCachePolicy.readEnabled) {
            diskCacheLazy.value.openSnapshot(diskCacheKey)
        } else {
            null
        }
    }

    /**
     * Snapshotからメタデータを読み取ります
     *
     * @return Snapshotから読み取ったメタデータ
     */
    private fun DiskCache.Snapshot.readMetadata(): T? {
        return try {
            fileSystem.read(metadata) {
                readMetadata()
            }
        } catch (_: Exception) {
            // If we can't parse the metadata, ignore this entry.
            null
        }
    }
}
