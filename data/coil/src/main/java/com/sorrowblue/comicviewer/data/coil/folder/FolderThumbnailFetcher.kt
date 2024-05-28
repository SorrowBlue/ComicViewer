package com.sorrowblue.comicviewer.data.coil.folder

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import androidx.core.graphics.scale
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.decode.DataSource
import coil3.disk.DiskCache
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import com.sorrowblue.comicviewer.data.coil.ThumbnailDiskCache
import com.sorrowblue.comicviewer.data.coil.abortQuietly
import com.sorrowblue.comicviewer.data.coil.book.CoilRuntimeException
import com.sorrowblue.comicviewer.data.coil.book.FileModelFetcher
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.settings.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import javax.inject.Inject
import kotlin.math.floor
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import logcat.asLog
import logcat.logcat
import okio.ByteString.Companion.encodeUtf8

internal class FolderThumbnailFetcher(
    private val folder: Folder,
    options: Options,
    diskCache: dagger.Lazy<DiskCache?>,
    private val fileModelLocalDataSource: FileLocalDataSource,
    private val datastoreDataSource: DatastoreDataSource,
) : FileModelFetcher(options, diskCache) {

    override suspend fun fetch(): FetchResult {
        val size = (requestWidth / (requestHeight / 11).toInt()).toInt() - 6
        val folderThumbnailOrder =
            datastoreDataSource.displaySettings.first().folderThumbnailOrder
        var snapshot = readFromDiskCache()
        try {
            if (snapshot != null) {
                // キャッシュされた画像は手動で追加された可能性が高いため、常にメタデータが空の状態で返されます。
                if (fileSystem.metadata(snapshot.metadata).size == 0L) {
                    return SourceFetchResult(
                        source = snapshot.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.DISK
                    )
                }
                // サムネイル候補キャッシュを取得
                val thumbnails = fileModelLocalDataSource.getCacheKeys(
                    folder.bookshelfId,
                    folder.path,
                    size,
                    FolderThumbnailOrder.valueOf(folderThumbnailOrder.name)
                )
                // 候補が適格である場合、キャッシュから候補を返します。
                if (snapshot.toFolderThumbnailMetadata() == FolderThumbnailMetadata(
                        folder.path, folder.bookshelfId.value, folder.lastModifier, thumbnails
                    )
                ) {
                    return SourceFetchResult(
                        source = snapshot.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.DISK
                    )
                }
            }
            try {
                val thumbnails = cacheList(size, folderThumbnailOrder)
                if (thumbnails.isEmpty()) {
                    throw CoilRuntimeException("フォルダにファイルなし。")
                    // キャッシュがない場合、取得する。
//                    val bookshelfModel =
//                        bookshelfLocalDataSource.flow(folder.bookshelfId).first()!!
//                    val supportExtensions =
//                        datastoreDataSource.folderSettings.first().supportExtension.map(
//                            SupportExtension::extension
//                        )
//                    snapshot = remoteDataSourceFactory.create(bookshelfModel)
//                        .listFiles(folder, false) { SortUtil.filter(it, supportExtensions) }.let {
//                            SortUtil.sortedIndex(it)
//                        }.firstOrNull { it is BookFile }?.let {
//                            val fileReader =
//                                remoteDataSourceFactory.create(bookshelfModel)
//                                    .fileReader(it as BookFile)
//                                    ?: throw CoilRuntimeException("FileReaderが取得できない")
//                            val bitmap = fileReader.thumbnailBitmap(
//                                requestWidth.toInt(), requestHeight.toInt()
//                            ) ?: throw CoilRuntimeException("画像を取得できない")
//                            writeToDiskCache(
//                                snapshot = snapshot, fileReader = fileReader, bitmap = bitmap
//                            )
//                        } ?: throw CoilRuntimeException("フォルダにファイルなし。")
//                    return SourceResult(
//                        source = snapshot.toImageSource(),
//                        mimeType = null,
//                        dataSource = DataSource.DISK
//                    )
                } else {
                    // 応答をディスク キャッシュに書き込み、新しいスナップショットを開きます。
                    snapshot = writeToDiskCache(snapshot = snapshot, list = thumbnails)
                    return if (snapshot != null) {
                        SourceFetchResult(
                            source = snapshot.toImageSource(),
                            mimeType = null,
                            dataSource = DataSource.DISK
                        )
                    } else {
                        SourceFetchResult(
                            source = readFromDiskCache()!!.toImageSource(),
                            mimeType = null,
                            dataSource = DataSource.DISK
                        )
                    }
                }
            } catch (e: Exception) {
                logcat { e.asLog() }
                throw e
            }
        } catch (e: Exception) {
            snapshot?.closeQuietly()
            throw e
        }
    }

    private suspend fun cacheList(
        size: Int,
        folderThumbnailOrder: FolderThumbnailOrder,
    ): List<Pair<String, DiskCache.Snapshot>> {
        val cacheKeyList = fileModelLocalDataSource.getCacheKeys(
            folder.bookshelfId,
            folder.path,
            size,
            FolderThumbnailOrder.valueOf(folderThumbnailOrder.name)
        )
        val notEnough = cacheKeyList.size < size
        val list = cacheKeyList.mapNotNull { cacheKey ->
            diskCache?.openSnapshot(cacheKey)?.let {
                cacheKey to it
            } ?: null.apply {
                fileModelLocalDataSource.removeCacheKey(cacheKey)
            }
        }
        return if (notEnough || size <= list.size) {
            list
        } else {
            list.forEach { it.second.closeQuietly() }
            cacheList(size, folderThumbnailOrder)
        }
    }

    private suspend fun writeToDiskCache(
        snapshot: DiskCache.Snapshot?,
        list: List<Pair<String, DiskCache.Snapshot>>,
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

        // このエントリに書き込めない場合は「null」を返します。
        if (editor == null) return null

        try {
            val result = Bitmap.createBitmap(
                requestWidth.toInt(),
                requestHeight.toInt(),
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(result)
            canvas.drawColor(Color.TRANSPARENT)
            val step = floor(requestHeight / 12)
            var left = 0f
            list.reversed().forEach {
                it.second.use { snap ->
                    combineThumbnail(canvas, snap, left)
                    left += step
                }
            }
            return withContext(NonCancellable) {
                fileSystem.write(editor.metadata) {
                    FolderThumbnailMetadata(
                        folder.path,
                        folder.bookshelfId.value,
                        folder.lastModifier,
                        list.map { it.first }
                    ).writeTo(this)
                }
                fileSystem.write(editor.data) {
                    outputStream().use {
                        result.compress(COMPRESS_FORMAT, 75, it)
                    }
                    result.recycle()
                }
                editor.commitAndOpenSnapshot()
            }
        } catch (e: Exception) {
            editor.abortQuietly()
            throw e
        }
    }

    private fun combineThumbnail(canvas: Canvas, snap: DiskCache.Snapshot, rightSpace: Float) {
        val bitmap = BitmapFactory.decodeFile(snap.data.toString())
        val resizeScale =
            if (bitmap.width >= bitmap.height) requestWidth / bitmap.width else requestHeight / bitmap.height
        val scale = bitmap.scale(
            (bitmap.width * resizeScale).toInt(),
            (bitmap.height * resizeScale).toInt(),
            true
        )
        bitmap.recycle()
        canvas.drawBitmap(scale, requestWidth - scale.width - rightSpace, 0f, null)
        scale.recycle()
    }

    override val diskCacheKey
        get() = options.diskCacheKey
            ?: "${folder.path}:${folder.bookshelfId.value}:${folder.lastModifier}".encodeUtf8()
                .sha256().hex()

    private fun DiskCache.Snapshot.toFolderThumbnailMetadata(): FolderThumbnailMetadata? {
        return try {
            fileSystem.read(metadata) {
                FolderThumbnailMetadata.from(this)
            }
        } catch (_: Exception) {
            // メタデータを解析できない場合は、このエントリを無視してください。
            null
        }
    }

    class Factory @Inject constructor(
        @ThumbnailDiskCache private val diskCache: dagger.Lazy<DiskCache?>,
        private val fileModelLocalDataSource: FileLocalDataSource,
        private val datastoreDataSource: DatastoreDataSource,
    ) : Fetcher.Factory<Folder> {

        override fun create(
            data: Folder,
            options: Options,
            imageLoader: ImageLoader,
        ): Fetcher {
            return FolderThumbnailFetcher(
                data,
                options,
                diskCache,
                fileModelLocalDataSource,
                datastoreDataSource
            )
        }
    }
}

fun AutoCloseable.closeQuietly() {
    kotlin.runCatching {
        close()
    }
}
