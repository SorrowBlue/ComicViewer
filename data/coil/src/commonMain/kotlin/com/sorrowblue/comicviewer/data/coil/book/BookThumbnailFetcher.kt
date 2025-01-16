package com.sorrowblue.comicviewer.data.coil.book

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
import com.sorrowblue.comicviewer.data.coil.resizeImage
import com.sorrowblue.comicviewer.data.coil.thumbnailDiskCache
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import kotlinx.coroutines.flow.first
import okio.Buffer
import okio.BufferedSource
import okio.ByteString.Companion.encodeUtf8
import okio.use
import org.koin.core.annotation.Singleton

internal class BookThumbnailFetcher(
    options: Options,
    diskCacheLazy: Lazy<DiskCache?>,
    private val data: BookThumbnail,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val fileLocalDataSource: FileLocalDataSource,
    private val datastoreDataSource: DatastoreDataSource,
) : FileFetcher<BookThumbnailMetadata>(options, diskCacheLazy) {

    override val diskCacheKey
        get() = options.diskCacheKey
            ?: "book:${data.bookshelfId.value}:${data.path}".encodeUtf8().sha256().hex()

    override suspend fun metadata() = BookThumbnailMetadata(data)

    override fun BufferedSource.readMetadata() =
        BookThumbnailMetadata.from<BookThumbnailMetadata>(this)

    override suspend fun fetch(): FetchResult? {
        // ページ数が不明
        return if (data.totalPageCount <= 0) {
            innerFetch(null)
        } else {
            super.fetch()
        }
    }

    override suspend fun innerFetch(snapshot: DiskCache.Snapshot?): FetchResult {
        val source = bookshelfLocalDataSource.flow(data.bookshelfId).first()
            ?.let(remoteDataSourceFactory::create)
            ?: throw CoilRuntimeException("本棚が取得できない")
        if (!source.exists(data.path)) {
            throw CoilRuntimeException("ファイルがない(${data.path})")
        }
        val book =
            fileLocalDataSource.flow(data.bookshelfId, data.path).first() as? Book
                ?: throw CoilRuntimeException("本が取得できない")
        val fileReader = source.fileReader(book)
            ?: throw CoilRuntimeException("FileReaderが取得できない")

        // 応答をディスク キャッシュに書き込み、新しいスナップショットを開きます。
        return writeToDiskCache(snapshot, BookThumbnailMetadata(data)) { sink ->
            val displaySettings = datastoreDataSource.folderDisplaySettings.first()
            val quality = displaySettings.thumbnailQuality
            val compressFormat = displaySettings.imageFormat
            Buffer().use {
                fileReader.copyTo(0, it)
                fileReader.closeQuietly()
                resizeImage(it, sink, compressFormat, quality)
            }
        }?.use {
            // DISKキャッシュキーとページ数を更新する。
            fileLocalDataSource.updateAdditionalInfo(
                data.path,
                data.bookshelfId,
                diskCacheKey,
                fileReader.pageCount()
            )
            fileReader.closeQuietly()
            return SourceFetchResult(
                source = it.toImageSource(),
                mimeType = null,
                dataSource = DataSource.NETWORK
            )
        } ?: run {
            // 新しいスナップショットの読み取りに失敗した場合は、応答本文が空でない場合はそれを読み取ります。
            Buffer().use { buffer ->
                fileReader.copyTo(0, buffer)
                fileReader.closeQuietly()
                SourceFetchResult(
                    source = buffer.toImageSource(),
                    mimeType = null,
                    dataSource = DataSource.NETWORK
                )
            }
        }
    }
}

@Singleton
@com.sorrowblue.comicviewer.data.coil.BookThumbnailFetcher
internal class BookThumbnailFetcherFactory(
    private val coilDiskCacheLazy: Lazy<CoilDiskCache>,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val fileModelLocalDataSource: FileLocalDataSource,
    private val datastoreDataSource: DatastoreDataSource,
) : Fetcher.Factory<BookThumbnail> {

    override fun create(
        data: BookThumbnail,
        options: Options,
        imageLoader: ImageLoader,
    ): Fetcher {
        return BookThumbnailFetcher(
            options = options,
            diskCacheLazy = lazy { coilDiskCacheLazy.value.thumbnailDiskCache(data.bookshelfId) },
            data = data,
            remoteDataSourceFactory = remoteDataSourceFactory,
            bookshelfLocalDataSource = bookshelfLocalDataSource,
            fileLocalDataSource = fileModelLocalDataSource,
            datastoreDataSource = datastoreDataSource
        )
    }
}
