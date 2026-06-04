package com.sorrowblue.comicviewer.data.coil.fetcher.book

import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.disk.DiskCache
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import com.sorrowblue.comicviewer.data.coil.cache.CoilDiskCache
import com.sorrowblue.comicviewer.data.coil.cache.thumbnailDiskCache
import com.sorrowblue.comicviewer.data.coil.closeQuietly
import com.sorrowblue.comicviewer.data.coil.di.CoilScope
import com.sorrowblue.comicviewer.data.coil.fetcher.BaseFetcher
import com.sorrowblue.comicviewer.data.coil.resizeImage
import com.sorrowblue.comicviewer.data.storage.client.FileClientFactory
import com.sorrowblue.comicviewer.data.storage.client.getFileClient
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import dev.zacsweers.metro.ClassKey
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import kotlinx.coroutines.flow.first
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.serialization.json.Json

internal class BookThumbnailFetcher(
    private val data: BookThumbnail,
    options: Options,
    diskCache: Lazy<DiskCache>,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val fileLocalDataSource: FileLocalDataSource,
    private val datastoreDataSource: DatastoreDataSource,
    private val fileClientFactory: FileClientFactory,
) : BaseFetcher<BookThumbnail, BookThumbnailMetadata>(data, options, diskCache) {

    override suspend fun doFetch(): FetchResult {
        var snapshot = readFromDiskCache()
        runCatching {
            // Fast path: fetch the image from the disk cache without performing a network request.
            val result = fastPath(snapshot)
            if (result != null) {
                return result
            }

            // Slow path: fetch the image from the network.
            val bookshelf = checkNotNull(bookshelfLocalDataSource.flow(data.bookshelfId).first()) {
                "Bookshelf not found. id: ${data.bookshelfId}"
            }
            val dataSource = remoteDataSourceFactory.create(bookshelf)
            check(dataSource.exists(data.path)) {
                "File not found. id: ${data.bookshelfId}, path: ${data.path}"
            }
            val book =
                checkNotNull(
                    fileLocalDataSource.flow(data.bookshelfId, data.path).first() as? Book,
                ) {
                    "Book not found. id: ${data.bookshelfId}, path: ${data.path}"
                }

            val fetchResult =
                fileClientFactory.getFileClient(bookshelf).fileReader(book).use { fileReader ->
                    check(fileReader.pageCount() != 0) {
                        "Only 0 pages"
                    }
                    val displaySettings = datastoreDataSource.folderDisplaySettings.first()
                    if (displaySettings.isSavedThumbnail) {
                        val quality = displaySettings.thumbnailQuality
                        val compressFormat = displaySettings.imageFormat
                        // 応答をディスク キャッシュに書き込み、新しいスナップショットを開きます。
                        snapshot =
                            writeToDiskCache(snapshot = snapshot, metaData = metadata()) { sink ->
                                fileReader.source(0).use { inputSource ->
                                    resizeImage(inputSource, sink, compressFormat, quality)
                                }
                            }

                        snapshot?.let {
                            // DISKキャッシュキーとページ数を更新する。
                            fileLocalDataSource.updateAdditionalInfo(
                                data.path,
                                data.bookshelfId,
                                diskCacheKey,
                                fileReader.pageCount(),
                            )
                            return@use SourceFetchResult(
                                source = it.toImageSource(),
                                mimeType = "image/*",
                                dataSource = DataSource.NETWORK,
                            )
                        }

                        // 新しいスナップショットの読み取りに失敗した場合は、応答本文が空でない場合はそれを読み取ります。
                        val source = fileReader.source(0)
                        // DISKキャッシュキーとページ数を更新する。
                        fileLocalDataSource.updateAdditionalInfo(
                            data.path,
                            data.bookshelfId,
                            diskCacheKey,
                            fileReader.pageCount(),
                        )
                        return@use SourceFetchResult(
                            source = source.toImageSource(),
                            mimeType = null,
                            dataSource = DataSource.NETWORK,
                        )
                    } else {
                        return@use SourceFetchResult(
                            source = fileReader.source(0).toImageSource(),
                            mimeType = null,
                            dataSource = DataSource.NETWORK,
                        )
                    }
                }
            return fetchResult
        }.onFailure {
            snapshot?.closeQuietly()
        }.getOrThrow()
    }

    override val diskCacheKey
        get() = options.diskCacheKey ?: "book:${data.bookshelfId.value}:${data.path}"

    override suspend fun metadata() = BookThumbnailMetadata(data)

    override fun readFrom(source: Source): BookThumbnailMetadata =
        Json.decodeFromString<BookThumbnailMetadata>(source.readString())

    override suspend fun fetch(): FetchResult? {
        // ページ数が不明な場合はキャッシュから取得する処理をスキップします。
        return if (data.totalPageCount <= 0) {
            doFetch()
        } else {
            super.fetch()
        }
    }

    @ClassKey(BookThumbnail::class)
    @ContributesIntoMap(CoilScope::class, binding = binding<coil3.key.Keyer<*>>())
    class Keyer : coil3.key.Keyer<BookThumbnail> {
        override fun key(data: BookThumbnail, options: Options) =
            "book:${data.bookshelfId.value}:${data.path}"
    }

    @ClassKey(BookThumbnail::class)
    @ContributesIntoMap(CoilScope::class, binding = binding<Fetcher.Factory<*>>())
    class Factory(
        private val coilDiskCacheLazy: Lazy<CoilDiskCache>,
        private val remoteDataSourceFactory: RemoteDataSource.Factory,
        private val bookshelfLocalDataSource: BookshelfLocalDataSource,
        private val fileModelLocalDataSource: FileLocalDataSource,
        private val datastoreDataSource: DatastoreDataSource,
        private val fileClientFactory: FileClientFactory,
    ) : Fetcher.Factory<BookThumbnail> {
        override fun create(
            data: BookThumbnail,
            options: Options,
            imageLoader: ImageLoader,
        ): Fetcher = BookThumbnailFetcher(
            data = data,
            options = options,
            diskCache = lazy {
                coilDiskCacheLazy.value.thumbnailDiskCache(data.bookshelfId)
            },
            remoteDataSourceFactory = remoteDataSourceFactory,
            bookshelfLocalDataSource = bookshelfLocalDataSource,
            fileLocalDataSource = fileModelLocalDataSource,
            datastoreDataSource = datastoreDataSource,
            fileClientFactory = fileClientFactory,
        )
    }
}
