package com.sorrowblue.comicviewer.data.remote

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
import com.sorrowblue.comicviewer.data.coil.SmbFetcherFactory
import com.sorrowblue.comicviewer.data.common.BookPageRequestData
import com.sorrowblue.comicviewer.data.remote.client.FileClientFactory
import com.sorrowblue.comicviewer.data.remote.di.PageDiskCache
import com.sorrowblue.comicviewer.data.remote.reader.FileReaderFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.InputStream
import javax.inject.Inject
import kotlin.reflect.KProperty
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import okhttp3.internal.closeQuietly
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8

@Serializable
internal data class BookPageMeta(
    val pageIndex: Int,
    val fileName: String = "",
    val fileSize: Long = 0
)

@OptIn(ExperimentalCoilApi::class)
internal fun DiskCache.Editor.abortQuietly() {
    try {
        abort()
    } catch (_: Exception) {
    }
}
operator fun <T> dagger.Lazy<T>.getValue(receiver: Any?, property: KProperty<*>): T = get()
@OptIn(ExperimentalCoilApi::class)
internal class SmbFetcher(
    private val data: BookPageRequestData,
    private val options: Options,
    lazyDiskCache: dagger.Lazy<DiskCache?>,
    private val context: Context,
    private val fileClientFactory: FileClientFactory,
    private val fileReaderFactory: FileReaderFactory,
) : Fetcher {

    private val diskCache by lazyDiskCache

    override suspend fun fetch(): FetchResult {
        var snapshot = readFromDiskCache()
        try {
            if (snapshot != null) {
                // キャッシュから返す。
                return SourceResult(
                    source = snapshot.toImageSource(),
                    mimeType = null,
                    dataSource = DataSource.DISK
                )
            }
            val client = fileClientFactory.create(data.serverModel)
            val fileReader = if (client.exists(data.fileModel)) {
                fileReaderFactory.create(client, data.fileModel)
            } else {
                null
            } ?: throw Exception("ファイルが見つかりません。path=${data.fileModel.path}")

            var response: InputStream = fileReader.pageInputStream(data.pageIndex)
            val fileSize = fileReader.fileSize(data.pageIndex)
            val fileName = fileReader.fileName(data.pageIndex)
            try {
                var bytes = response.readBytes()
                snapshot = writeToDiskCache(snapshot, bytes, fileName, fileSize)
                if (snapshot != null) {
                    return SourceResult(
                        source = snapshot.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.NETWORK
                    )
                }
                if (bytes.isNotEmpty()) {
                    return SourceResult(
                        source = ImageSource(Buffer().apply { write(bytes) }, context),
                        mimeType = null,
                        dataSource = DataSource.NETWORK
                    )
                } else {
                    response.closeQuietly()
                    response = fileReader.pageInputStream(data.pageIndex)
                    bytes = response.use { it.readBytes() }
                    return SourceResult(
                        source = ImageSource(Buffer().apply { write(bytes) }, context),
                        mimeType = null,
                        dataSource = DataSource.NETWORK
                    )
                }
            } catch (e: Exception) {
                response.closeQuietly()
                throw e
            } finally {
                response.closeQuietly()
                fileReader.closeQuietly()
            }
        } catch (e: Exception) {
            snapshot?.closeQuietly()
            throw e
        }
    }

    private fun readFromDiskCache(): DiskCache.Snapshot? {
        return if (options.diskCachePolicy.readEnabled) {
            diskCache?.get(diskCacheKey)
        } else {
            null
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun writeToDiskCache(
        snapshot: DiskCache.Snapshot?,
        response: ByteArray,
        fileName: String,
        fileSize: Long
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
                Json.encodeToStream(
                    BookPageMeta(data.pageIndex, fileName, fileSize),
                    outputStream()
                )
            }
            fileSystem.write(editor.data) {
                write(response)
            }
            editor.commitAndGet()
        }.onFailure {
            editor.abortQuietly()
        }.getOrThrow()
    }

    private fun DiskCache.Snapshot.toImageSource(): ImageSource {
        return ImageSource(data, fileSystem, diskCacheKey, this)
    }

    private val diskCacheKey
        get() = options.diskCacheKey
            ?: "${data.fileModel.path}?index=${data.pageIndex}".encodeUtf8()
                .sha256().hex()

    private val fileSystem get() = diskCache!!.fileSystem

    class Factory @Inject constructor(
        @PageDiskCache
        private val diskCache: dagger.Lazy<DiskCache?>,
        @ApplicationContext private val context: Context,
        private val fileClientFactory: FileClientFactory,
        private val fileReaderFactory: FileReaderFactory,
    ) : SmbFetcherFactory {

        override fun create(
            data: BookPageRequestData,
            options: Options,
            imageLoader: ImageLoader
        ): Fetcher {
            return SmbFetcher(data, options, diskCache, context, fileClientFactory, fileReaderFactory)
        }
    }
}
