/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

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
import com.sorrowblue.comicviewer.data.coil.fetcher.BaseFetcher
import com.sorrowblue.comicviewer.data.coil.fetcher.CoilMetadata
import com.sorrowblue.comicviewer.data.coil.resizeImage
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import com.sorrowblue.comicviewer.domain.repository.storage.RemoteStorageClient
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ClassKey
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import kotlinx.coroutines.flow.first
import kotlinx.io.Source

internal class BookThumbnailFetcher(
    private val data: BookThumbnail,
    options: Options,
    diskCache: Lazy<DiskCache>,
    private val remoteStorageClientFactory: RemoteStorageClient.Factory,
    private val bookshelfRepository: BookshelfRepository,
    private val fileRepository: FileRepository,
    private val settingsRepository: SettingsRepository,
) : BaseFetcher<BookThumbnailMetadata>(options, diskCache) {

    override suspend fun doFetch(): FetchResult {
        var snapshot = readFromDiskCache()
        runCatching {
            // Fast path: fetch the image from the disk cache without performing a network request.
            val cachedResult = fastPath(snapshot)
            if (cachedResult != null && data.totalPageCount > 0) {
                return cachedResult
            }

            val book =
                checkNotNull(
                    fileRepository.flow(data.bookshelfId, data.path).first() as? Book,
                ) {
                    "Book not found. id: ${data.bookshelfId}, path: ${data.path}"
                }
            if (cachedResult != null && book.totalPageCount > 0) {
                if (book.cacheKey != diskCacheKey) {
                    fileRepository.updateAdditionalInfo(
                        data.path,
                        data.bookshelfId,
                        diskCacheKey,
                        book.totalPageCount,
                    )
                }
                return cachedResult
            }

            // Slow path: fetch the image from the network.
            val bookshelf = checkNotNull(bookshelfRepository.flow(data.bookshelfId).first()) {
                "Bookshelf not found. id: ${data.bookshelfId}"
            }
            val storageClient = remoteStorageClientFactory.create(bookshelf)
            check(storageClient.exists(data.path)) {
                "File not found. id: ${data.bookshelfId}, path: ${data.path}"
            }

            val fetchResult =
                storageClient.fileReader(book).use { fileReader ->
                    val pageCount = fileReader.pageCount()
                    check(pageCount != 0) {
                        "Only 0 pages"
                    }
                    if (cachedResult != null) {
                        if (book.cacheKey != diskCacheKey || book.totalPageCount != pageCount) {
                            fileRepository.updateAdditionalInfo(
                                data.path,
                                data.bookshelfId,
                                diskCacheKey,
                                pageCount,
                            )
                        }
                        return@use cachedResult
                    }
                    val displaySettings = settingsRepository.folderDisplaySettings.first()
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
                            if (book.cacheKey != diskCacheKey || book.totalPageCount != pageCount) {
                                fileRepository.updateAdditionalInfo(
                                    data.path,
                                    data.bookshelfId,
                                    diskCacheKey,
                                    pageCount,
                                )
                            }
                            return@use SourceFetchResult(
                                source = it.toImageSource(),
                                mimeType = "image/*",
                                dataSource = DataSource.DISK,
                            )
                        }
                    } else {
                        snapshot?.closeQuietly()
                    }

                    // 新しいスナップショットの読み取りに失敗した場合、またはサムネイル保存が無効な場合は応答本文を読み取ります。
                    val source = fileReader.source(0)
                    if (book.totalPageCount != pageCount) {
                        fileRepository.updateAdditionalInfo(
                            data.path,
                            data.bookshelfId,
                            book.cacheKey,
                            pageCount,
                        )
                    }
                    return@use SourceFetchResult(
                        source = source.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.NETWORK,
                    )
                }
            return fetchResult
        }.onFailure {
            snapshot?.closeQuietly()
        }.getOrThrow()
    }

    override val diskCacheKey
        get() = options.diskCacheKey ?: "book:${data.bookshelfId.value}:${data.path}"

    override suspend fun metadata() = BookThumbnailMetadata(data)

    override fun readFrom(source: Source): BookThumbnailMetadata = CoilMetadata.from(source)

    @ClassKey(BookThumbnail::class)
    @ContributesIntoMap(AppScope::class, binding = binding<coil3.key.Keyer<*>>())
    class Keyer : coil3.key.Keyer<BookThumbnail> {
        override fun key(data: BookThumbnail, options: Options) =
            "book:${data.bookshelfId.value}:${data.path}"
    }

    @ClassKey(BookThumbnail::class)
    @ContributesIntoMap(AppScope::class, binding = binding<Fetcher.Factory<*>>())
    class Factory(
        private val coilDiskCacheLazy: Lazy<CoilDiskCache>,
        private val remoteStorageClientFactory: RemoteStorageClient.Factory,
        private val bookshelfRepository: BookshelfRepository,
        private val fileRepository: FileRepository,
        private val settingsRepository: SettingsRepository,
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
            remoteStorageClientFactory = remoteStorageClientFactory,
            bookshelfRepository = bookshelfRepository,
            fileRepository = fileRepository,
            settingsRepository = settingsRepository,
        )
    }
}
