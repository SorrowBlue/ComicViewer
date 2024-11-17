package com.sorrowblue.comicviewer.data.coil.book

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.disk.DiskCache
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import com.sorrowblue.comicviewer.data.coil.CoilRuntimeException
import com.sorrowblue.comicviewer.data.coil.FileFetcher
import com.sorrowblue.comicviewer.data.coil.di.CoilDiskCache
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import okio.Buffer
import okio.BufferedSource
import okio.ByteString.Companion.encodeUtf8
import okio.use

internal class BookThumbnailFetcher(
    options: Options,
    diskCacheLazy: dagger.Lazy<DiskCache?>,
    private val data: BookThumbnail,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val fileLocalDataSource: FileLocalDataSource,
    private val datastoreDataSource: DatastoreDataSource,
) : FileFetcher<BookThumbnailMetadata>(options, diskCacheLazy) {

    override val diskCacheKey
        get() = options.diskCacheKey
            ?: "id:${data.bookshelfId.value},path:${data.path}".encodeUtf8().sha256().hex()

    override suspend fun metadata() = BookThumbnailMetadata(data)

    override fun BufferedSource.readMetadata() = BookThumbnailMetadata.from(this)

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
            val compressFormat = displaySettings.imageFormat.toCompressFormat()
            Buffer().use {
                fileReader.copyTo(0, it)
                BitmapFactory.decodeStream(it.inputStream())
            }.let { bitmap ->
                bitmap.compress(compressFormat, quality, sink.outputStream())
                bitmap.recycle()
            }
        }?.use {
            // DISKキャッシュキーとページ数を更新する。
            fileLocalDataSource.updateAdditionalInfo(
                data.path,
                data.bookshelfId,
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

    private fun ImageFormat.toCompressFormat() = when (this) {
        ImageFormat.WEBP -> Bitmap.CompressFormat.WEBP_LOSSY
        ImageFormat.JPEG -> Bitmap.CompressFormat.JPEG
        ImageFormat.PNG -> Bitmap.CompressFormat.PNG
    }

    class Factory @Inject constructor(
        @ApplicationContext private val context: Context,
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
                diskCacheLazy = { CoilDiskCache.thumbnailDiskCache(context, data.bookshelfId) },
                data = data,
                remoteDataSourceFactory = remoteDataSourceFactory,
                bookshelfLocalDataSource = bookshelfLocalDataSource,
                fileLocalDataSource = fileModelLocalDataSource,
                datastoreDataSource = datastoreDataSource
            )
        }
    }
}
