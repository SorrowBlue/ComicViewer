package com.sorrowblue.comicviewer.data.coil.book

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.exifinterface.media.ExifInterface
import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.decode.DecodeUtils
import coil3.decode.ImageSource
import coil3.disk.DiskCache
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import coil3.size.Scale
import com.sorrowblue.comicviewer.data.coil.ThumbnailDiskCache
import com.sorrowblue.comicviewer.data.coil.abortQuietly
import com.sorrowblue.comicviewer.data.coil.folder.closeQuietly
import com.sorrowblue.comicviewer.data.reader.FileReader
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.InputStream
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import logcat.asLog
import logcat.logcat
import okio.ByteString.Companion.encodeUtf8
import okio.buffer
import okio.source

private const val MIME_IMAGE_WEBP = "image/webp"

internal class BookThumbnailFetcher(
    private val book: Book,
    options: Options,
    diskCache: dagger.Lazy<DiskCache?>,
    private val context: Context,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val fileModelLocalDataSource: FileLocalDataSource,
) : FileModelFetcher(options, diskCache) {

    override suspend fun fetch(): FetchResult {
        var snapshot = readFromDiskCache()
        try {
            // 高速パス: ネットワーク要求を実行せずに、ディスク キャッシュからイメージをフェッチする。
            if (snapshot != null) {
                // キャッシュされた画像は手動で追加された可能性が高いため、常にメタデータが空の状態で返されます。
                if (fileSystem.metadata(snapshot.metadata).size == 0L) {
                    return SourceFetchResult(
                        source = snapshot.toImageSource(),
                        mimeType = MIME_IMAGE_WEBP,
                        dataSource = DataSource.DISK
                    )
                }
                // 候補が適格である場合、キャッシュから候補を返します。
                if (snapshot.toBookThumbnailMetadata() == BookThumbnailMetadata(book)) {
                    return SourceFetchResult(
                        source = snapshot.toImageSource(),
                        mimeType = MIME_IMAGE_WEBP,
                        dataSource = DataSource.DISK
                    )
                }
            }
            val source = bookshelfLocalDataSource.flow(book.bookshelfId).first()
                ?.let(remoteDataSourceFactory::create)
                ?: throw CoilRuntimeException("本棚が取得できない")
            if (!source.exists(book.path)) {
                throw CoilRuntimeException("ファイルがない(${book.path})")
            }
            val fileReader = source.fileReader(book)
                ?: throw CoilRuntimeException("FileReaderが取得できない")
            try {
                // 応答をディスク キャッシュに書き込み、新しいスナップショットを開きます。
                snapshot = fileReader.pageInputStream(0).use {
                    writeToDiskCache(
                        snapshot = snapshot,
                        inputStream = it,
                        metadata = BookThumbnailMetadata(book)
                    )
                }
                if (snapshot != null) {
                    // DISKキャッシュキーとページ数を更新する。
                    fileModelLocalDataSource.updateAdditionalInfo(
                        book.path,
                        book.bookshelfId,
                        diskCacheKey,
                        fileReader.pageCount()
                    )
                    return SourceFetchResult(
                        source = snapshot.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.NETWORK
                    )
                }

                // 新しいスナップショットの読み取りに失敗した場合は、応答本文が空でない場合はそれを読み取ります。
                return fileReader.pageInputStream(0).use {
                    SourceFetchResult(
                        source = it.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.NETWORK
                    )
                }
            } catch (e: Exception) {
                logcat { e.asLog() }
                throw e
            } finally {
                fileReader.closeQuietly()
            }
        } catch (e: Exception) {
            snapshot?.closeQuietly()
            throw e
        }
    }

    override val diskCacheKey
        get() = options.diskCacheKey
            ?: "${book.path}:${book.bookshelfId.value}:${book.lastModifier}".encodeUtf8()
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

    private fun writeToDiskCache(
        snapshot: DiskCache.Snapshot?,
        inputStream: InputStream,
        metadata: BookThumbnailMetadata,
    ): DiskCache.Snapshot? {
        // この応答をキャッシュすることが許可されていない場合は短絡します。
        if (!isCacheable()) {
            snapshot?.closeQuietly()
            return null
        }

        // 新しいエディターを開きます。
        val editor = if (snapshot != null) {
            snapshot.closeAndOpenEditor()
        } else {
            diskCache?.openEditor(diskCacheKey)
        }

        // このエントリに書き込めない場合は 'null' を返します。
        if (editor == null) return null

        try {
            // 応答をディスク キャッシュに書き込みます。
            // メタデータと画像データを更新します。
            fileSystem.write(editor.metadata) {
                metadata.writeTo(this)
            }
            inputStream.use {
                fileSystem.write(editor.data) {
                    it.source().buffer().readAll(this)
                }
            }
            return editor.commitAndOpenSnapshot()
        } catch (e: Exception) {
            editor.abortQuietly()
            throw e
        }
    }

    private fun InputStream.toImageSource(): ImageSource {
        return ImageSource(source = source().buffer(), fileSystem = options.fileSystem)
    }

    class Factory @Inject constructor(
        @ThumbnailDiskCache private val diskCache: dagger.Lazy<DiskCache?>,
        @ApplicationContext private val context: Context,
        private val remoteDataSourceFactory: RemoteDataSource.Factory,
        private val bookshelfLocalDataSource: BookshelfLocalDataSource,
        private val fileModelLocalDataSource: FileLocalDataSource,
    ) : Fetcher.Factory<Book> {

        override fun create(
            data: Book,
            options: Options,
            imageLoader: ImageLoader,
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

class CoilRuntimeException(message: String?) : RuntimeException(message)
