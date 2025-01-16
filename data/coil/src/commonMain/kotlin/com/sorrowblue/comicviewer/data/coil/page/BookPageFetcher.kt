package com.sorrowblue.comicviewer.data.coil.page

import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.disk.DiskCache
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import com.sorrowblue.comicviewer.data.coil.CoilDiskCache
import com.sorrowblue.comicviewer.data.coil.CoilRuntimeException
import com.sorrowblue.comicviewer.data.coil.FileFetcher
import com.sorrowblue.comicviewer.data.coil.closeQuietly
import com.sorrowblue.comicviewer.data.coil.from
import com.sorrowblue.comicviewer.data.coil.pageDiskCache
import com.sorrowblue.comicviewer.domain.model.BookPageRequest
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.reader.FileReader
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import logcat.asLog
import logcat.logcat
import okio.Buffer
import okio.BufferedSource
import okio.ByteString.Companion.encodeUtf8
import okio.use
import org.koin.core.annotation.Singleton

private var fileReader: FileReader? = null
private var book: Book? = null

private val mutex = Mutex()

internal class BookPageFetcher(
    options: Options,
    diskCacheLazy: Lazy<DiskCache?>,
    private val data: BookPageRequest,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
) : FileFetcher<BookPageMetaData>(options, diskCacheLazy) {

    override suspend fun metadata(): BookPageMetaData {
        return BookPageMetaData(data.pageIndex, data.book.name, data.book.size)
    }

    override fun BufferedSource.readMetadata() = BookPageMetaData.from<BookPageMetaData>(this)

    override suspend fun innerFetch(snapshot: DiskCache.Snapshot?): FetchResult {
        val remoteDataSource = bookshelfLocalDataSource.flow(data.book.bookshelfId).first()
            ?.let(remoteDataSourceFactory::create)
            ?: remoteDataSourceFactory.create(InternalStorage(BookshelfId(), "intent"))
        if (!remoteDataSource.exists(data.book.path)) {
            throw CoilRuntimeException("ファイルがない(${data.book.path})")
        }
        val fileReader = mutex.withLock {
            if (fileReader != null && book?.bookshelfId == data.book.bookshelfId && book?.path == data.book.path) {
                logcat { "同じFileReaderを使う。 ${data.book.name}, ${data.pageIndex}" }
                fileReader!!
            } else {
                logcat { "新しいFileReaderを使う。 ${data.book.name}, ${data.pageIndex}" }
                fileReader?.closeQuietly()
                remoteDataSource.fileReader(data.book)?.also {
                    book = data.book
                    fileReader = it
                } ?: throw CoilRuntimeException("FileReaderが取得できない")
            }
        }
        try {
            // 応答をディスク キャッシュに書き込み、新しいスナップショットを開きます。
            return writeToDiskCache(snapshot = snapshot, metaData = metadata()) {
                fileReader.copyTo(data.pageIndex, it)
            }?.use { snapshot1 ->
                SourceFetchResult(
                    source = snapshot1.toImageSource(),
                    mimeType = null,
                    dataSource = DataSource.NETWORK
                )
            } ?: run {
                // 新しいスナップショットの読み取りに失敗した場合は、応答本文が空でない場合はそれを読み取ります。
                Buffer().use { buffer ->
                    fileReader.copyTo(data.pageIndex, buffer)
                    SourceFetchResult(
                        source = buffer.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.NETWORK
                    )
                }
            }
        } catch (e: Exception) {
            logcat { e.asLog() }
            throw e
        }
    }

    override val diskCacheKey
        get() = options.diskCacheKey
            ?: "id:${data.book.bookshelfId.value},path:${data.book.path},index:${data.pageIndex}".encodeUtf8()
                .sha256().hex()

}

@Singleton
@com.sorrowblue.comicviewer.data.coil.BookPageFetcher
internal class BookPageFetcherFactory(
    private val coilDiskCacheLazy: Lazy<CoilDiskCache>,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
) : Fetcher.Factory<BookPageRequest> {

    override fun create(data: BookPageRequest, options: Options, imageLoader: ImageLoader) =
        BookPageFetcher(
            options,
            lazy { coilDiskCacheLazy.value.pageDiskCache(data.book.bookshelfId) },
            data,
            remoteDataSourceFactory,
            bookshelfLocalDataSource
        )
}
