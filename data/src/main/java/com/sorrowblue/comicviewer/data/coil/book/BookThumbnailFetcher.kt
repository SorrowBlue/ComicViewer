package com.sorrowblue.comicviewer.data.coil.book

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.exifinterface.media.ExifInterface
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.decode.DataSource
import coil.decode.DecodeUtils
import coil.decode.ImageSource
import coil.disk.DiskCache
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.request.Options
import coil.size.Scale
import com.sorrowblue.comicviewer.data.coil.abortQuietly
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.FileModelLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.data.di.ThumbnailDiskCache
import com.sorrowblue.comicviewer.data.remote.reader.FileReader
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.InputStream
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import logcat.logcat
import okhttp3.internal.closeQuietly
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8

@OptIn(ExperimentalCoilApi::class)
internal class BookThumbnailFetcher(
    private val book: FileModel.Book,
    options: Options,
    diskCache: dagger.Lazy<DiskCache?>,
    private val context: Context,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val fileModelLocalDataSource: FileModelLocalDataSource
) : FileModelFetcher(options, diskCache) {

    override suspend fun fetch(): FetchResult {
        var snapshot = readFromDiskCache()
        try {
            if (snapshot != null) {
                // キャッシュされた画像は手動で追加された可能性が高いため、常にメタデータが空の状態で返されます。
                if (fileSystem.metadata(snapshot.metadata).size == 0L) {
                    logcat { "キャッシュされた画像は手動で追加された可能性が高いため、常にメタデータが空の状態で返されます。" }
                    return SourceResult(
                        source = snapshot.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.DISK
                    )
                }

                // 候補が適格である場合、キャッシュから候補を返します。
                if (snapshot.toBookThumbnailMetadata() == BookThumbnailMetadata(book)) {
                    logcat { "候補が適格である場合、キャッシュから候補を返します。" }
                    return SourceResult(
                        source = snapshot.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.DISK
                    )
                }
            }
            val bookshelfModel = bookshelfLocalDataSource.get(book.bookshelfModelId).first()
                ?: throw RuntimeException("本棚が取得できない")
            logcat { "server" }
            var fileReader = remoteDataSourceFactory.create(bookshelfModel).fileReader(book)
                ?: throw RuntimeException("FileReaderが取得できない")
            logcat { "fileReader" }
            val bitmap = fileReader.thumbnailBitmap(requestWidth.toInt(), requestHeight.toInt())
                ?: throw RuntimeException("画像を取得できない")
            logcat { "bitmap" }
            try {
                // 応答をディスク キャッシュに書き込み、新しいスナップショットを開きます。
                snapshot =
                    writeToDiskCache(snapshot = snapshot, fileReader = fileReader, bitmap = bitmap)
                if (snapshot != null) {
                    logcat { "応答をディスク キャッシュに書き込み、新しいスナップショットを開きます。" }
                    return SourceResult(
                        source = snapshot.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.NETWORK
                    )
                }
                // 新しいスナップショットの読み取りに失敗した場合は、応答本文が空でない場合は読み取ります。
                var bytes = fileReader.pageInputStream(0).use(InputStream::readBytes)
                if (bytes.isNotEmpty()) {
                    logcat { "新しいスナップショットの読み取りに失敗した場合は、応答本文が空でない場合は読み取ります。" }
                    return SourceResult(
                        source = ImageSource(Buffer().apply { write(bytes) }, context),
                        mimeType = null,
                        dataSource = DataSource.NETWORK
                    )
                } else {
                    fileReader.closeQuietly()
                    fileReader = remoteDataSourceFactory.create(bookshelfModel).fileReader(book)
                        ?: throw RuntimeException("FileReaderが取得できない")
                    bytes = fileReader.pageInputStream(0).use(InputStream::readBytes)
                    return SourceResult(
                        source = ImageSource(Buffer().apply { write(bytes) }, context),
                        mimeType = null,
                        dataSource = DataSource.NETWORK
                    )
                }
            } catch (e: Exception) {
                bitmap.recycle()
                fileReader.closeQuietly()
                throw e
            }
        } catch (e: Exception) {
            snapshot?.closeQuietly()
            throw e
        }
    }

    private suspend fun writeToDiskCache(
        snapshot: DiskCache.Snapshot?, fileReader: FileReader, bitmap: Bitmap
    ): DiskCache.Snapshot? {
        // この応答をキャッシュすることが許可されていない場合は短絡します。
        if (!isCacheable()) {
            snapshot?.closeQuietly()
            return null
        }

        // 新しいエディターを開きます。
        val editor = if (snapshot != null) {
            snapshot.closeAndEdit()
        } else {
            diskCache?.edit(diskCacheKey)
        }

        // このエントリに書き込めない場合は「null」を返します。
        if (editor == null) return null

        try {
            // 応答をディスク キャッシュに書き込みます。
            // メタデータと画像データを更新します。
            fileSystem.write(editor.metadata) {
                BookThumbnailMetadata(book).writeTo(this)
            }
            fileSystem.write(editor.data) {
                bitmap.compress(COMPRESS_FORMAT, 75, outputStream())
            }
            // DISKキャッシュキーとページ数を更新する。
            fileModelLocalDataSource.update(
                book.path, book.bookshelfModelId, diskCacheKey, fileReader.pageCount()
            )
            return editor.commitAndGet()
        } catch (e: Exception) {
            editor.abortQuietly()
            throw e
        } finally {
            bitmap.recycle()
            fileReader.closeQuietly()
        }
    }

    override val diskCacheKey
        get() = options.diskCacheKey
            ?: "${book.path}:${book.bookshelfModelId.value}:${book.lastModifier}".encodeUtf8()
                .sha256().hex()

    private fun DiskCache.Snapshot.toBookThumbnailMetadata(): BookThumbnailMetadata? {
        return try {
            fileSystem.read(metadata) {
                BookThumbnailMetadata.from(this)
            }
        } catch (_: Exception) {
            // メタデータを解析できない場合は、このエントリを無視してください。
            null
        }
    }

    class Factory @Inject constructor(
        @ThumbnailDiskCache private val diskCache: dagger.Lazy<DiskCache?>,
        @ApplicationContext private val context: Context,
        private val remoteDataSourceFactory: RemoteDataSource.Factory,
        private val bookshelfLocalDataSource: BookshelfLocalDataSource,
        private val fileModelLocalDataSource: FileModelLocalDataSource,
    ) : Fetcher.Factory<FileModel.Book> {

        override fun create(
            data: FileModel.Book, options: Options, imageLoader: ImageLoader
        ): Fetcher {
            return BookThumbnailFetcher(
                data,
                options,
                diskCache,
                context,
                remoteDataSourceFactory,
                bookshelfLocalDataSource,
                fileModelLocalDataSource
            )
        }
    }
}

internal suspend fun FileReader.thumbnailBitmap(width: Int, height: Int): Bitmap? {
    return pageInputStream(0).use {
        ExifInterface(it).run { if (hasThumbnail()) thumbnailBitmap else null }
    } ?: BitmapFactory.Options().run {
        inJustDecodeBounds = true
        pageInputStream(0).use { BitmapFactory.decodeStream(it, null, this) }
        inSampleSize =
            DecodeUtils.calculateInSampleSize(outWidth, outHeight, width, height, Scale.FIT)
        inJustDecodeBounds = false
        pageInputStream(0).use { BitmapFactory.decodeStream(it, null, this) }
    }
}
