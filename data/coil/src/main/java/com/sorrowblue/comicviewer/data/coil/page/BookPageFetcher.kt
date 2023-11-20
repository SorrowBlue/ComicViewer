package com.sorrowblue.comicviewer.data.coil.page

import android.content.Context
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.decode.DataSource
import coil.decode.ImageSource
import coil.disk.DiskCache
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.request.Options
import com.sorrowblue.comicviewer.data.coil.CoilFetcher
import com.sorrowblue.comicviewer.data.coil.PageDiskCache
import com.sorrowblue.comicviewer.data.coil.abortQuietly
import com.sorrowblue.comicviewer.data.coil.book.CoilRuntimeException
import com.sorrowblue.comicviewer.data.infrastructure.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.data.infrastructure.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.data.reader.FileReader
import com.sorrowblue.comicviewer.domain.model.BookPageRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import logcat.asLog
import logcat.logcat
import okhttp3.internal.closeQuietly
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8

@OptIn(ExperimentalCoilApi::class)
internal class BookPageFetcher(
    private val data: BookPageRequest,
    private val options: Options,
    diskCacheLazy: dagger.Lazy<DiskCache?>,
    private val context: Context,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
) : CoilFetcher<BookPageRequest>(options, diskCacheLazy) {

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

    override suspend fun fetch(): FetchResult {
        var snapshot = readFromDiskCache()
        try {
            if (snapshot != null) {
                if (fileSystem.metadata(snapshot.metadata).size == 0L) {
                    return SourceResult(
                        source = snapshot.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.DISK
                    )
                }
                if (snapshot.toBookPageMetaData() != null) {
                    return SourceResult(
                        source = snapshot.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.DISK
                    )
                }
            }
            var fileReader: FileReader? = null
            try {
                fileReader =
                    remoteDataSourceFactory.create(
                        bookshelfLocalDataSource.flow(data.book.bookshelfId).first()!!
                    ).fileReader(data.book)
                        ?: throw CoilRuntimeException("この拡張子はサポートされていません。")
                var inputStream: InputStream = fileReader.pageInputStream(data.pageIndex)
                var bytes = inputStream.use { it.readBytes() }
                val metaData = BookPageMetaData(
                    data.pageIndex,
                    fileReader.fileName(data.pageIndex),
                    fileReader.fileSize(data.pageIndex)
                )
                snapshot = writeToDiskCache(snapshot, bytes, metaData)
                if (snapshot != null) {
                    return SourceResult(
                        source = snapshot.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.NETWORK
                    )
                }
                return if (bytes.isNotEmpty()) {
                    SourceResult(
                        source = ImageSource(Buffer().apply { write(bytes) }, context),
                        mimeType = null,
                        dataSource = DataSource.NETWORK
                    )
                } else {
                    inputStream = fileReader.pageInputStream(data.pageIndex)
                    bytes = inputStream.use { it.readBytes() }
                    SourceResult(
                        source = ImageSource(Buffer().apply { write(bytes) }, context),
                        mimeType = null,
                        dataSource = DataSource.NETWORK
                    )
                }
            } catch (e: Exception) {
                logcat { e.asLog() }
                throw e
            } finally {
                fileReader?.closeQuietly()
            }
        } catch (e: Exception) {
            snapshot?.closeQuietly()
            throw e
        }
    }

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
        bytes: ByteArray,
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
                metaData.write(outputStream())
            }
            fileSystem.write(editor.data) {
                write(bytes)
            }
            editor.commitAndOpenSnapshot()
        }.onFailure {
            editor.abortQuietly()
        }.getOrThrow()
    }

    override val diskCacheKey
        get() = options.diskCacheKey
            ?: "${data.book.path}?index=${data.pageIndex}".encodeUtf8().sha256().hex()
}
