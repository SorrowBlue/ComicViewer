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
import com.sorrowblue.comicviewer.data.coil.di.CoilScope
import com.sorrowblue.comicviewer.data.coil.fetcher.CoilMetadata
import com.sorrowblue.comicviewer.data.coil.fetcher.FileFetcher
import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.FileClientType
import com.sorrowblue.comicviewer.data.storage.client.FileReader
import com.sorrowblue.comicviewer.domain.model.BookPageImage
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.DeviceStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import dev.zacsweers.metro.ClassKey
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import logcat.asLog
import logcat.logcat
import okio.Buffer
import okio.BufferedSource

private var fileReader: FileReader? = null
private var book: Book? = null

private val mutex = Mutex()

internal class BookPageImageFetcher(
    options: Options,
    diskCacheLazy: Lazy<DiskCache>,
    private val data: BookPageImage,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val fileClientFactory: Map<FileClientType, FileClient.Factory<*>>,
) : FileFetcher<BookPageImageMetadata>(options, diskCacheLazy) {

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
        private val fileClientFactory: Map<FileClientType, FileClient.Factory<*>>,
    ) : Fetcher.Factory<BookPageImage> {
        override fun create(data: BookPageImage, options: Options, imageLoader: ImageLoader) =
            BookPageImageFetcher(
                options,
                lazy { coilDiskCacheLazy.value.pageDiskCache(data.book.bookshelfId) },
                data,
                remoteDataSourceFactory,
                bookshelfLocalDataSource,
                fileClientFactory,
            )
    }

    @Suppress("UNCHECKED_CAST")
    private fun fileClient(bookshelf: Bookshelf) = when (bookshelf) {
        is DeviceStorage -> fileClientFactory.getValue(
            FileClientType.Device,
        ) as FileClient.Factory<Bookshelf>

        is SmbServer -> fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>

        ShareContents -> fileClientFactory.getValue(
            FileClientType.Share,
        ) as FileClient.Factory<Bookshelf>
    }.create(bookshelf)

    override val diskCacheKey
        get() = options.diskCacheKey
            ?: "id:${data.book.bookshelfId.value},path:${data.book.path},index:${data.pageIndex}"

    override suspend fun metadata() = BookPageImageMetadata(data)

    override fun BufferedSource.readMetadata() = CoilMetadata.from<BookPageImageMetadata>(this)

    override suspend fun innerFetch(snapshot: DiskCache.Snapshot?): FetchResult {
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
        val fileReader = findFileReader() ?: fileClient(bookshelf).fileReader(data.book).also {
            book = data.book
            fileReader = it
        }
        return runCatching {
            // 応答をディスク キャッシュに書き込み、新しいスナップショットを開きます。
            writeToDiskCache(snapshot = snapshot, metaData = metadata()) { sink ->
                fileReader.copyTo(data.pageIndex, sink)
            }?.let { snapshot1 ->
                SourceFetchResult(
                    source = snapshot1.toImageSource(),
                    mimeType = null,
                    dataSource = DataSource.NETWORK,
                )
            } ?: run {
                // 新しいスナップショットの読み取りに失敗した場合は、応答本文が空でない場合はそれを読み取ります。
                Buffer().let {
                    fileReader.copyTo(data.pageIndex, it)
                    SourceFetchResult(
                        source = it.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.NETWORK,
                    )
                }
            }
        }.onFailure {
            logcat { it.asLog() }
        }.getOrThrow()
    }

    private suspend fun findFileReader(): FileReader? = mutex.withLock {
        fileReader?.takeIf { book == data.book }?.also {
            logcat { "同じFileReaderを使う。${data.book.bookshelfId} ${data.book.path}" }
        }
    }
}
