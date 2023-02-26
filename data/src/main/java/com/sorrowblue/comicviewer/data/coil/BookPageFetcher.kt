package com.sorrowblue.comicviewer.data.coil

import android.content.Context
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.decode.DataSource
import coil.decode.ImageSource
import coil.disk.DiskCache
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.request.Options
import com.sorrowblue.comicviewer.data.coil.meta.BookPageMetaData
import com.sorrowblue.comicviewer.data.common.BookPageRequestData
import com.sorrowblue.comicviewer.data.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.data.di.PageDiskCache
import com.sorrowblue.comicviewer.data.remote.reader.FileReader
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.InputStream
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import okhttp3.internal.closeQuietly
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8

@OptIn(ExperimentalCoilApi::class)
internal class BookPageFetcher(
    private val data: BookPageRequestData,
    private val options: Options,
    diskCacheLazy: dagger.Lazy<DiskCache?>,
    private val context: Context,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
) : CoilFetcher<BookPageRequestData>(data, options, diskCacheLazy) {

    class Factory @Inject constructor(
        @PageDiskCache private val diskCache: dagger.Lazy<DiskCache?>,
        @ApplicationContext private val context: Context,
        private val remoteDataSourceFactory: RemoteDataSource.Factory,
        private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    ) : Fetcher.Factory<BookPageRequestData> {

        override fun create(data: BookPageRequestData, options: Options, imageLoader: ImageLoader) =
            BookPageFetcher(data, options, diskCache, context, remoteDataSourceFactory, bookshelfLocalDataSource)
    }

    override suspend fun fetch(): FetchResult {
        var snapshot = readFromDiskCache()
        try {
            if (snapshot != null) {
                return SourceResult(
                    source = snapshot.toImageSource(),
                    mimeType = null,
                    dataSource = DataSource.DISK
                )
            }
            var fileReader: FileReader? = null
            try {
                fileReader =
                    remoteDataSourceFactory.create(bookshelfLocalDataSource.get(data.fileModel.bookshelfModelId).first()!!).fileReader(data.fileModel)
                        ?: throw RuntimeException("この拡張子はサポートされていません。")
                var inputStream: InputStream = fileReader.pageInputStream(data.pageIndex)
                var bytes = inputStream.use { it.readBytes() }
                val metaData = BookPageMetaData(
                    data.pageIndex,
                    fileReader.fileName(data.pageIndex),
                    fileReader.fileSize(data.pageIndex)
                )
                snapshot = writeToDiskCache(snapshot, bytes, metaData)
                if (snapshot != null) {
                    return SourceResult(
                        source = snapshot.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.NETWORK
                    )
                }
                return if (bytes.isNotEmpty()) {
                    SourceResult(
                        source = ImageSource(Buffer().apply { write(bytes) }, context),
                        mimeType = null,
                        dataSource = DataSource.NETWORK
                    )
                } else {
                    inputStream = fileReader.pageInputStream(data.pageIndex)
                    bytes = inputStream.use { it.readBytes() }
                    SourceResult(
                        source = ImageSource(Buffer().apply { write(bytes) }, context),
                        mimeType = null,
                        dataSource = DataSource.NETWORK
                    )
                }
            } catch (e: Exception) {
                throw e
            } finally {
                fileReader?.closeQuietly()
            }
        } catch (e: Exception) {
            snapshot?.closeQuietly()
            throw e
        }
    }

    private fun writeToDiskCache(
        snapshot: DiskCache.Snapshot?,
        bytes: ByteArray,
        metaData: BookPageMetaData
    ): DiskCache.Snapshot? {

        // 新しいエディターを開きます。
        val editor = if (snapshot != null) {
            snapshot.closeAndEdit()
        } else {
            diskCache?.edit(diskCacheKey)
        }

        // このエントリに書き込めない場合は `null` を返します。
        if (editor == null) return null

        // 応答をディスク キャッシュに書き込みます。
        // メタデータと画像データを更新します。
        return kotlin.runCatching {
            fileSystem.write(editor.metadata) {
                metaData.write(outputStream())
            }
            fileSystem.write(editor.data) {
                write(bytes)
            }
            editor.commitAndGet()
        }.onFailure {
            editor.abortQuietly()
        }.getOrThrow()
    }

    override val diskCacheKey
        get() = options.diskCacheKey
            ?: "${data.fileModel.path}?index=${data.pageIndex}".encodeUtf8().sha256().hex()
}
