package com.sorrowblue.comicviewer.data.coil.folder

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import androidx.core.graphics.scale
import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.disk.DiskCache
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import com.sorrowblue.comicviewer.data.coil.CoilRuntimeException
import com.sorrowblue.comicviewer.data.coil.FileFetcher
import com.sorrowblue.comicviewer.data.coil.abortQuietly
import com.sorrowblue.comicviewer.data.coil.book.FileModelFetcher.Companion.COMPRESS_FORMAT
import com.sorrowblue.comicviewer.data.coil.closeQuietly
import com.sorrowblue.comicviewer.data.coil.di.ThumbnailDiskCache
import com.sorrowblue.comicviewer.data.coil.from
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.settings.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import javax.inject.Inject
import kotlin.math.floor
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okio.BufferedSource
import okio.ByteString.Companion.encodeUtf8

internal class FolderThumbnailFetcher(
    private val folder: Folder,
    options: Options,
    diskCache: dagger.Lazy<DiskCache?>,
    private val fileModelLocalDataSource: FileLocalDataSource,
    private val datastoreDataSource: DatastoreDataSource,
) : FileFetcher<FolderThumbnailMetadata>(options, diskCache) {

    override suspend fun fetchRemote(snapshot: DiskCache.Snapshot?): FetchResult {
        val size = (requestWidth / (requestHeight / 11).toInt()).toInt() - 6
        val folderThumbnailOrder =
            datastoreDataSource.displaySettings.first().folderThumbnailOrder
        val thumbnails = cacheList(size, folderThumbnailOrder)
        if (thumbnails.isEmpty()) {
            throw CoilRuntimeException("フォルダにファイルなし。")
        } else {
            // 応答をディスク キャッシュに書き込み、新しいスナップショットを開きます。
            return writeToDiskCache(snapshot = snapshot, list = thumbnails)?.use { snapshot1 ->
                SourceFetchResult(
                    source = snapshot1.toImageSource(),
                    mimeType = null,
                    dataSource = DataSource.DISK
                )
            } ?: run {
                throw CoilRuntimeException("フォルダのサムネイル生成失敗。")
            }
        }
    }

    override val diskCacheKey
        get() = options.diskCacheKey
            ?: "${folder.path}:${folder.bookshelfId.value}:${folder.lastModifier}".encodeUtf8()
                .sha256().hex()

    override suspend fun metadata(): FolderThumbnailMetadata {
        val size = (requestWidth / (requestHeight / 11).toInt()).toInt() - 6
        val folderThumbnailOrder =
            datastoreDataSource.displaySettings.first().folderThumbnailOrder
        val thumbnails = fileModelLocalDataSource.getCacheKeys(
            folder.bookshelfId,
            folder.path,
            size,
            FolderThumbnailOrder.valueOf(folderThumbnailOrder.name)
        )
        return FolderThumbnailMetadata(
            folder.path,
            folder.bookshelfId.value,
            folder.lastModifier,
            thumbnails
        )
    }

    override fun BufferedSource.metadata() =
        FolderThumbnailMetadata.from<FolderThumbnailMetadata>(this)

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
        if (!isCacheable) {
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

    class Factory @Inject constructor(
        @ThumbnailDiskCache private val diskCache: dagger.Lazy<DiskCache?>,
        private val fileModelLocalDataSource: FileLocalDataSource,
        private val datastoreDataSource: DatastoreDataSource,
    ) : Fetcher.Factory<Folder> {

        override fun create(data: Folder, options: Options, imageLoader: ImageLoader): Fetcher {
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
