package com.sorrowblue.comicviewer.data.coil.page

import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.disk.DiskCache
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import com.sorrowblue.comicviewer.data.coil.CoilDiskCache
import com.sorrowblue.comicviewer.data.coil.CoilMetadata
import com.sorrowblue.comicviewer.data.coil.CoilRuntimeException
import com.sorrowblue.comicviewer.data.coil.FileFetcher
import com.sorrowblue.comicviewer.data.coil.closeQuietly
import com.sorrowblue.comicviewer.data.coil.pageDiskCache
import com.sorrowblue.comicviewer.domain.model.BookPageImage
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.service.FileReader
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import logcat.asLog
import logcat.logcat
import okio.Buffer
import okio.BufferedSource
import org.koin.core.annotation.Single

private var fileReader: FileReader? = null
private var book: Book? = null

private val mutex = Mutex()

internal class BookPageImageFetcher(
    options: Options,
    diskCacheLazy: Lazy<DiskCache>,
    private val data: BookPageImage,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
) : FileFetcher<BookPageImageMetadata>(options, diskCacheLazy) {

    override val diskCacheKey
        get() = options.diskCacheKey
            ?: "id:${data.book.bookshelfId.value},path:${data.book.path},index:${data.pageIndex}"

    override suspend fun metadata() = BookPageImageMetadata(data)

    override fun BufferedSource.readMetadata() = CoilMetadata.from<BookPageImageMetadata>(this)

    override suspend fun innerFetch(snapshot: DiskCache.Snapshot?): FetchResult {
        val dataSource = bookshelfLocalDataSource.flow(data.book.bookshelfId).first()
            ?.let(remoteDataSourceFactory::create)
            ?: remoteDataSourceFactory.create(ShareContents)
        if (!dataSource.exists(data.book.path)) {
            throw CoilRuntimeException("ファイルがない(${data.book.path})")
        }
        val fileReader = mutex.withLock {
            if (fileReader != null && book?.bookshelfId == data.book.bookshelfId && book?.path == data.book.path && book?.totalPageCount == data.book.totalPageCount && book?.lastModifier == data.book.lastModifier) {
                logcat { "同じFileReaderを使う。 ${data.book.name}, ${data.pageIndex}" }
                fileReader!!
            } else {
                logcat { "新しいFileReaderを使う。 ${data.book.name}, ${data.pageIndex}" }
                fileReader?.closeQuietly()
                dataSource.fileReader(data.book)?.also {
                    book = data.book
                    fileReader = it
                } ?: throw CoilRuntimeException("FileReaderが取得できない")
            }
        }
        try {
            // 応答をディスク キャッシュに書き込み、新しいスナップショットを開きます。
            return writeToDiskCache(snapshot = snapshot, metaData = metadata()) { sink ->
                fileReader.copyTo(data.pageIndex, sink)
            }?.let { snapshot1 ->
                SourceFetchResult(
                    source = snapshot1.toImageSource(),
                    mimeType = null,
                    dataSource = DataSource.NETWORK
                )
            } ?: run {
                // 新しいスナップショットの読み取りに失敗した場合は、応答本文が空でない場合はそれを読み取ります。
                Buffer().let {
                    fileReader.copyTo(data.pageIndex, it)
                    SourceFetchResult(
                        source = it.toImageSource(),
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
}

@Single
@com.sorrowblue.comicviewer.data.coil.BookPageImageFetcher
internal class BookPageImageFetcherFactory(
    private val coilDiskCacheLazy: Lazy<CoilDiskCache>,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
) : Fetcher.Factory<BookPageImage> {

    override fun create(data: BookPageImage, options: Options, imageLoader: ImageLoader) =
        BookPageImageFetcher(
            options,
            lazy { coilDiskCacheLazy.value.pageDiskCache(data.book.bookshelfId) },
            data,
            remoteDataSourceFactory,
            bookshelfLocalDataSource
        )
}
