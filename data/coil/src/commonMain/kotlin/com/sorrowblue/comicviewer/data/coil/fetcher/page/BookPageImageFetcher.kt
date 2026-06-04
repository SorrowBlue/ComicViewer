package com.sorrowblue.comicviewer.data.coil.fetcher.page

import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.disk.DiskCache
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import com.sorrowblue.comicviewer.data.coil.cache.CoilDiskCache
import com.sorrowblue.comicviewer.data.coil.cache.pageDiskCache
import com.sorrowblue.comicviewer.data.coil.closeQuietly
import com.sorrowblue.comicviewer.data.coil.di.CoilScope
import com.sorrowblue.comicviewer.data.coil.fetcher.BaseFetcher
import com.sorrowblue.comicviewer.data.coil.fetcher.CoilMetadata
import com.sorrowblue.comicviewer.data.coil.resizeImage
import com.sorrowblue.comicviewer.data.storage.client.FileClientFactory
import com.sorrowblue.comicviewer.data.storage.client.FileReader
import com.sorrowblue.comicviewer.data.storage.client.getFileClient
import com.sorrowblue.comicviewer.domain.model.BookPageImage
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import dev.zacsweers.metro.ClassKey
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.io.Source
import logcat.asLog
import logcat.logcat

private var fileReader: FileReader? = null
private var book: Book? = null
private val mutex = Mutex()

internal class BookPageImageFetcher(
    private val data: BookPageImage,
    options: Options,
    diskCacheLazy: Lazy<DiskCache>,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val datastoreDataSource: DatastoreDataSource,
    private val fileClientFactory: FileClientFactory,
) : BaseFetcher<BookPageImage, BookPageImageMetadata>(data, options, diskCacheLazy) {

    override val diskCacheKey
        get() = options.diskCacheKey
            ?: "id:${data.book.bookshelfId.value},path:${data.book.path},index:${data.pageIndex}"

    override suspend fun doFetch(): FetchResult {
        var snapshot = readFromDiskCache()
        runCatching {
            // Fast path: fetch the image from the disk cache without performing a network request.
            val result = fastPath(snapshot)
            if (result != null) {
                return result
            }
            val bookshelf = if (data.book.bookshelfId == BookshelfId()) {
                ShareContents
            } else {
                checkNotNull(bookshelfLocalDataSource.flow(data.book.bookshelfId).first()) {
                    "Bookshelf not found. id: ${data.book.bookshelfId}"
                }
            }
            val dataSource = remoteDataSourceFactory.create(bookshelf)
            check(dataSource.exists(data.book.path)) {
                "File not found. id: ${data.book.bookshelfId}, path: ${data.book.path}"
            }
            val fileReader =
                findFileReader() ?: fileClientFactory.getFileClient(bookshelf).fileReader(data.book)
                    .also {
                        book = data.book
                        fileReader = it
                    }
            val viewerSettings = datastoreDataSource.viewerSettings.first()
            val quality = viewerSettings.imageQuality
            val compressFormat = viewerSettings.imageFormat
            return runCatching {
                // 応答をディスク キャッシュに書き込み、新しいスナップショットを開きます。
                snapshot = writeToDiskCache(snapshot = snapshot, metaData = metadata()) { sink ->
                    if (compressFormat == ImageFormat.ORIGINAL) {
                        fileReader.extractTo(data.pageIndex, sink)
                    } else {
                        fileReader.source(data.pageIndex).use { inputSource ->
                            resizeImage(
                                inputSource,
                                sink,
                                compressFormat,
                                quality,
                            )
                        }
                    }
                }
                snapshot?.let {
                    SourceFetchResult(
                        source = it.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.NETWORK,
                    )
                }
                // 新しいスナップショットの読み取りに失敗した場合は、応答本文が空でない場合はそれを読み取ります。
                SourceFetchResult(
                    source = fileReader.source(data.pageIndex).toImageSource(),
                    mimeType = null,
                    dataSource = DataSource.NETWORK,
                )
            }.onFailure {
                logcat { it.asLog() }
            }.getOrThrow()
        }.onFailure {
            snapshot?.closeQuietly()
        }.getOrThrow()
    }

    override suspend fun metadata(): BookPageImageMetadata = BookPageImageMetadata(data)

    override fun readFrom(source: Source): BookPageImageMetadata =
        CoilMetadata.from<BookPageImageMetadata>(source)

    private suspend fun findFileReader(): FileReader? = mutex.withLock {
        fileReader?.takeIf { book == data.book }?.also {
            logcat { "同じFileReaderを使う。${data.book.bookshelfId} ${data.book.path}" }
        }
    }

    @ClassKey(BookPageImage::class)
    @ContributesIntoMap(CoilScope::class, binding = binding<coil3.key.Keyer<*>>())
    class Keyer : coil3.key.Keyer<BookPageImage> {
        override fun key(data: BookPageImage, options: Options) =
            "id:${data.book.bookshelfId.value},path:${data.book.path},index:${data.pageIndex}"
    }

    @ClassKey(BookPageImage::class)
    @ContributesIntoMap(CoilScope::class, binding = binding<Fetcher.Factory<*>>())
    class Factory(
        private val coilDiskCacheLazy: Lazy<CoilDiskCache>,
        private val remoteDataSourceFactory: RemoteDataSource.Factory,
        private val bookshelfLocalDataSource: BookshelfLocalDataSource,
        private val datastoreDataSource: DatastoreDataSource,
        private val fileClientFactory: FileClientFactory,
    ) : Fetcher.Factory<BookPageImage> {
        override fun create(data: BookPageImage, options: Options, imageLoader: ImageLoader) =
            BookPageImageFetcher(
                data,
                options,
                lazy { coilDiskCacheLazy.value.pageDiskCache(data.book.bookshelfId) },
                remoteDataSourceFactory,
                bookshelfLocalDataSource,
                datastoreDataSource,
                fileClientFactory,
            )
    }
}
