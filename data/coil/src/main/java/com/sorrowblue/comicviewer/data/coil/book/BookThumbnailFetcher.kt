package com.sorrowblue.comicviewer.data.coil.book

import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.disk.DiskCache
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import com.sorrowblue.comicviewer.data.coil.CoilRuntimeException
import com.sorrowblue.comicviewer.data.coil.FileFetcher
import com.sorrowblue.comicviewer.data.coil.di.ThumbnailDiskCache
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import logcat.logcat
import okio.Buffer
import okio.BufferedSource
import okio.ByteString.Companion.encodeUtf8
import okio.use

internal class BookThumbnailFetcher(
    options: Options,
    diskCacheLazy: dagger.Lazy<DiskCache?>,
    private val book: Book,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val fileModelLocalDataSource: FileLocalDataSource,
) : FileFetcher<BookThumbnailMetadata>(options, diskCacheLazy) {
    override suspend fun fetchRemote(snapshot: DiskCache.Snapshot?): FetchResult {
        val source = bookshelfLocalDataSource.flow(book.bookshelfId).first()
            ?.let(remoteDataSourceFactory::create)
            ?: throw CoilRuntimeException("本棚が取得できない")
        if (!source.exists(book.path)) {
            throw CoilRuntimeException("ファイルがない(${book.path})")
        }
        val fileReader = source.fileReader(book)
            ?: throw CoilRuntimeException("FileReaderが取得できない")

        // 応答をディスク キャッシュに書き込み、新しいスナップショットを開きます。
        return writeToDiskCache(snapshot, BookThumbnailMetadata(book)) {
            fileReader.copyTo(0, it)
        }?.use {
            // DISKキャッシュキーとページ数を更新する。
            logcat { "DISKキャッシュキーとページ数を更新する。$book" }
            fileModelLocalDataSource.updateAdditionalInfo(
                book.path,
                book.bookshelfId,
                diskCacheKey,
                fileReader.pageCount()
            )
            return SourceFetchResult(
                source = it.toImageSource(),
                mimeType = null,
                dataSource = DataSource.NETWORK
            )
        } ?: run {
            // 新しいスナップショットの読み取りに失敗した場合は、応答本文が空でない場合はそれを読み取ります。
            logcat { "新しいスナップショットの読み取りに失敗した場合は、応答本文が空でない場合はそれを読み取ります。$book" }
            Buffer().use { buffer ->
                fileReader.copyTo(0, buffer)
                SourceFetchResult(
                    source = buffer.toImageSource(),
                    mimeType = null,
                    dataSource = DataSource.NETWORK
                )
            }
        }
    }

    override suspend fun metadata() = BookThumbnailMetadata(book)

    override fun BufferedSource.metadata() = BookThumbnailMetadata.from(this)

    override val diskCacheKey
        get() = options.diskCacheKey
            ?: "${book.path}:${book.bookshelfId.value}:${book.lastModifier}".encodeUtf8()
                .sha256().hex()

    class Factory @Inject constructor(
        @ThumbnailDiskCache private val diskCache: dagger.Lazy<DiskCache?>,
        private val remoteDataSourceFactory: RemoteDataSource.Factory,
        private val bookshelfLocalDataSource: BookshelfLocalDataSource,
        private val fileModelLocalDataSource: FileLocalDataSource,
    ) : Fetcher.Factory<Book> {

        override fun create(data: Book, options: Options, imageLoader: ImageLoader): Fetcher {
            return BookThumbnailFetcher(
                options,
                diskCache,
                data,
                remoteDataSourceFactory,
                bookshelfLocalDataSource,
                fileModelLocalDataSource
            )
        }
    }
}
