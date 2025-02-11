package com.sorrowblue.comicviewer.data.coil

import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.disk.DiskCache
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import coil3.size.pxOrElse
import com.sorrowblue.comicviewer.domain.reader.asKotlinxIoRawSink
import com.sorrowblue.comicviewer.domain.reader.asOkioSource
import kotlinx.io.InternalIoApi
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.buffered
import okio.buffer

/**
 * File fetcher
 *
 * @param T メタデータ
 * @param diskCacheLazy
 * @constructor
 * @property options
 */
internal abstract class FileFetcher<T : CoilMetaData>(
    val options: Options,
    diskCacheLazy: Lazy<DiskCache?>,
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
    abstract fun Source.readMetadata(): T?

    /**
     * Inner fetch
     *
     * @param snapshot
     * @return
     */
    abstract suspend fun innerFetch(snapshot: DiskCache.Snapshot?): FetchResult?

    override suspend fun fetch(): FetchResult? {
        return readFromDiskCache()?.use { snapshot ->
            // 高速パス: ネットワーク要求を実行せずに、ディスク キャッシュからイメージをフェッチする。
            // キャッシュされた画像は手動で追加された可能性が高いため、常にメタデータが空の状態で返されます。
            if (fileSystem.metadata(snapshot.metadata).size == 0L) {
                return SourceFetchResult(
                    source = snapshot.toImageSource(),
                    mimeType = null,
                    dataSource = DataSource.DISK
                )
            }
            // 候補が適格である場合、キャッシュから候補を返します。
            if (snapshot.readMetadata() == metadata()) {
                return SourceFetchResult(
                    source = snapshot.toImageSource(),
                    mimeType = null,
                    dataSource = DataSource.DISK
                )
            }
            diskCache?.remove(diskCacheKey)
            innerFetch(snapshot)
        } ?: run {
            innerFetch(null)
        }
    }

    protected val diskCache by diskCacheLazy
    protected val requestWidth = options.size.width.pxOrElse { 300 }.toFloat()
    protected val requestHeight = options.size.height.pxOrElse { 300 }.toFloat()

    protected suspend fun writeToDiskCache(
        snapshot: DiskCache.Snapshot?,
        metaData: CoilMetaData,
        sink: suspend (Sink) -> Unit,
    ): DiskCache.Snapshot? {
        // この応答をキャッシュすることが許可されていない場合は、ショートします。
        if (!isCacheable) {
            snapshot?.closeQuietly()
            return null
        }

        // 新しいエディターを開きます。
        val editor = if (snapshot != null) {
            snapshot.closeAndOpenEditor()
        } else {
            diskCache?.openEditor(diskCacheKey)
        }

        // このエントリに書き込めない場合は `null` を返します。
        if (editor == null) return null

        // 応答をディスク キャッシュに書き込みます。
        // メタデータと画像データを更新します。
        return kotlin.runCatching {
            fileSystem.write(editor.metadata) {
                metaData.writeTo(asKotlinxIoRawSink().buffered())
            }
            fileSystem.write(editor.data) {
                sink(asKotlinxIoRawSink().buffered())
            }
            editor.commitAndOpenSnapshot()
        }.onFailure {
            editor.abortQuietly()
        }.getOrThrow()
    }

    private val isCacheable get() = options.diskCachePolicy.writeEnabled

    protected fun DiskCache.Snapshot.toImageSource() =
        ImageSource(data, fileSystem, diskCacheKey, this)

    @OptIn(InternalIoApi::class)
    protected fun Sink.toImageSource() =
        ImageSource(source = buffer.asOkioSource().buffer(), fileSystem = options.fileSystem)

    private val fileSystem get() = diskCache!!.fileSystem

    private fun readFromDiskCache(): DiskCache.Snapshot? {
        return if (options.diskCachePolicy.readEnabled) {
            diskCache?.openSnapshot(diskCacheKey)
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
