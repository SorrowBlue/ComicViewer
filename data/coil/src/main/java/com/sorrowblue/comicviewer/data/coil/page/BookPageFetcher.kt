package com.sorrowblue.comicviewer.data.coil.page

import android.content.Context
import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.disk.DiskCache
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import com.sorrowblue.comicviewer.data.coil.PageDiskCache
import com.sorrowblue.comicviewer.data.coil.abortQuietly
import com.sorrowblue.comicviewer.data.coil.book.CoilRuntimeException
import com.sorrowblue.comicviewer.data.coil.book.FileModelFetcher
import com.sorrowblue.comicviewer.data.coil.folder.closeQuietly
import com.sorrowblue.comicviewer.domain.model.BookPageRequest
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import kotlin.use
import kotlinx.coroutines.flow.first
import logcat.asLog
import logcat.logcat
import okio.ByteString.Companion.encodeUtf8
import okio.buffer
import okio.source

internal class BookPageFetcher(
    private val data: BookPageRequest,
    options: Options,
    diskCacheLazy: dagger.Lazy<DiskCache?>,
    private val context: Context,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
) : FileModelFetcher(options, diskCacheLazy) {

    override suspend fun fetch(): FetchResult {
        var snapshot = readFromDiskCache()
        try {
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
                if (snapshot.toBookPageMetaData() != null) {
                    return SourceFetchResult(
                        source = snapshot.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.DISK
                    )
                }
            }

            val source = bookshelfLocalDataSource.flow(data.book.bookshelfId).first()
                ?.let(remoteDataSourceFactory::create)
                ?: throw CoilRuntimeException("本棚が取得できない")
            if (!source.exists(data.book.path)) {
                throw CoilRuntimeException("ファイルがない(${data.book.path})")
            }
            val fileReader = source.fileReader(data.book)
                ?: throw CoilRuntimeException("FileReaderが取得できない")
            try {
                val metaData = BookPageMetaData(
                    data.pageIndex,
                    fileReader.fileName(data.pageIndex),
                    fileReader.fileSize(data.pageIndex)
                )
                // 応答をディスク キャッシュに書き込み、新しいスナップショットを開きます。
                snapshot = fileReader.pageInputStream(data.pageIndex).use {
                    writeToDiskCache(snapshot = snapshot, inputStream = it, metaData = metaData)
                }
                if (snapshot != null) {
                    return SourceFetchResult(
                        source = snapshot.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.NETWORK
                    )
                }
                // 新しいスナップショットの読み取りに失敗した場合は、応答本文が空でない場合はそれを読み取ります。
                return fileReader.pageInputStream(data.pageIndex).use {
                    SourceFetchResult(
                        source = it.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.NETWORK
                    )
                }
            } catch (e: Exception) {
                logcat { e.asLog() }
                throw e
            } finally {
                fileReader.closeQuietly()
            }
        } catch (e: Exception) {
            snapshot?.closeQuietly()
            throw e
        }
    }

    override val diskCacheKey
        get() = options.diskCacheKey
            ?: "${data.book.path}?index=${data.pageIndex}".encodeUtf8().sha256().hex()

    private fun DiskCache.Snapshot.toBookPageMetaData(): BookPageMetaData? {
        return try {
            fileSystem.read(metadata) {
                BookPageMetaData.from(this)
            }
        } catch (_: IOException) {
            // If we can't parse the metadata, ignore this entry.
            null
        }
    }

    private fun writeToDiskCache(
        snapshot: DiskCache.Snapshot?,
        inputStream: InputStream,
        metaData: BookPageMetaData,
    ): DiskCache.Snapshot? {
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
                writeAll(inputStream.source())
            }
            editor.commitAndOpenSnapshot()
        }.onFailure {
            editor.abortQuietly()
        }.getOrThrow()
    }

    private fun InputStream.toImageSource(): ImageSource {
        return ImageSource(source().buffer(), options.fileSystem)
    }

    class Factory @Inject constructor(
        @PageDiskCache private val diskCache: dagger.Lazy<DiskCache?>,
        @ApplicationContext private val context: Context,
        private val remoteDataSourceFactory: RemoteDataSource.Factory,
        private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    ) : Fetcher.Factory<BookPageRequest> {

        override fun create(data: BookPageRequest, options: Options, imageLoader: ImageLoader) =
            BookPageFetcher(
                data,
                options,
                diskCache,
                context,
                remoteDataSourceFactory,
                bookshelfLocalDataSource
            )
    }
}
