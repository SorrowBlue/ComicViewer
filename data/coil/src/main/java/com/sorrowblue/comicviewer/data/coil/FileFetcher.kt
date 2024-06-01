package com.sorrowblue.comicviewer.data.coil

import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.disk.DiskCache
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import coil3.size.pxOrElse
import java.io.IOException
import okio.BufferedSink
import okio.BufferedSource
import okio.Sink
import okio.buffer

internal abstract class FileFetcher<T : CoilMetaData>(
    val options: Options,
    diskCacheLazy: dagger.Lazy<DiskCache?>,
) : Fetcher {

    abstract suspend fun fetchRemote(snapshot: DiskCache.Snapshot?): FetchResult?
    abstract val diskCacheKey: String
    abstract suspend fun metadata(): T
    abstract fun BufferedSource.metadata(): T?

    final override suspend fun fetch(): FetchResult? {
        readFromDiskCache().use { snapshot ->
            // 高速パス: ネットワーク要求を実行せずに、ディスク キャッシュからイメージをフェッチする。
            if (snapshot != null) {
                // キャッシュされた画像は手動で追加された可能性が高いため、常にメタデータが空の状態で返されます。
                if (fileSystem.metadata(snapshot.metadata).size == 0L) {
                    return SourceFetchResult(
                        source = snapshot.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.DISK
                    )
                }
                // 候補が適格である場合、キャッシュから候補を返します。
                if (snapshot.metadata() == metadata()) {
                    return SourceFetchResult(
                        source = snapshot.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.DISK
                    )
                }
            }
            return fetchRemote(snapshot)
        }
    }

    protected val diskCache by diskCacheLazy
    protected val requestWidth = options.size.width.pxOrElse { 300 }.toFloat()
    protected val requestHeight = options.size.height.pxOrElse { 300 }.toFloat()

    protected suspend fun writeToDiskCache(
        snapshot: DiskCache.Snapshot?,
        metaData: CoilMetaData,
        sink: suspend (BufferedSink) -> Unit,
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
                metaData.writeTo(this)
            }
            fileSystem.write(editor.data) {
                sink(this)
            }
            editor.commitAndOpenSnapshot()
        }.onFailure {
            editor.abortQuietly()
        }.getOrThrow()
    }

    protected val isCacheable get() = options.diskCachePolicy.writeEnabled

    protected fun DiskCache.Snapshot.toImageSource() =
        ImageSource(data, fileSystem, diskCacheKey, this)

    protected fun Sink.toImageSource() =
        ImageSource(source = buffer().buffer, fileSystem = options.fileSystem)

    protected val fileSystem get() = diskCache!!.fileSystem

    private fun readFromDiskCache(): DiskCache.Snapshot? {
        return if (options.diskCachePolicy.readEnabled) {
            diskCache?.openSnapshot(diskCacheKey)
        } else {
            null
        }
    }

    private fun DiskCache.Snapshot.metadata(): T? {
        return try {
            fileSystem.read(metadata) {
                metadata()
            }
        } catch (_: IOException) {
            // If we can't parse the metadata, ignore this entry.
            null
        }
    }
}
