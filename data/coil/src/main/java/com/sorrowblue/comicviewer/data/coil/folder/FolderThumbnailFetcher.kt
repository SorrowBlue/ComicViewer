package com.sorrowblue.comicviewer.data.coil.folder

import android.content.Context
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
import com.sorrowblue.comicviewer.data.coil.book.FileModelFetcher.Companion.COMPRESS_FORMAT
import com.sorrowblue.comicviewer.data.coil.closeQuietly
import com.sorrowblue.comicviewer.data.coil.from
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.settings.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlinx.coroutines.flow.first
import okio.BufferedSource
import okio.ByteString.Companion.encodeUtf8
import okio.use

private typealias PxSize = Pair<Int, Int>

internal class FolderThumbnailFetcher(
    private val folder: Folder,
    options: Options,
    diskCache: dagger.Lazy<DiskCache?>,
    private val fileModelLocalDataSource: FileLocalDataSource,
    private val datastoreDataSource: DatastoreDataSource,
) : FileFetcher<FolderThumbnailMetadata>(options, diskCache) {

    override val diskCacheKey
        get() = options.diskCacheKey
            ?: "${folder.path}:${folder.bookshelfId.value}:${folder.lastModifier}".encodeUtf8()
                .sha256().hex()

    override suspend fun metadata(): FolderThumbnailMetadata {
        val folderThumbnailOrder =
            datastoreDataSource.displaySettings.first().folderThumbnailOrder
        val thumbnails = getThumbnailCacheList(5, folderThumbnailOrder)
        thumbnails.forEach { it.second.closeQuietly() }
        return FolderThumbnailMetadata(
            folder.path,
            folder.bookshelfId.value,
            folder.lastModifier,
            thumbnails.map { it.first }
        )
    }

    override fun BufferedSource.readMetadata() =
        FolderThumbnailMetadata.from<FolderThumbnailMetadata>(this)

    override suspend fun innerFetch(snapshot: DiskCache.Snapshot?): FetchResult {
        val folderThumbnailOrder =
            datastoreDataSource.displaySettings.first().folderThumbnailOrder
        val thumbnails = getThumbnailCacheList(5, folderThumbnailOrder)
        if (thumbnails.isEmpty()) {
            throw CoilRuntimeException("There are no book thumbnails in this folder.")
        } else {
            val pxSize = calculateMaxThumbnailSize(thumbnails)
            val bitmap = createFolderThumbnailBitmap(thumbnails, pxSize)
            // 応答をディスク キャッシュに書き込み、新しいスナップショットを開きます。
            return writeToDiskCache(snapshot = snapshot, metaData = metadata()) { sink ->
                bitmap.compress(COMPRESS_FORMAT, 75, sink.outputStream())
                bitmap.recycle()
            }?.use {
                return SourceFetchResult(
                    source = it.toImageSource(),
                    mimeType = null,
                    dataSource = DataSource.DISK
                )
            } ?: run {
                throw CoilRuntimeException("フォルダのサムネイル生成失敗。")
            }
        }
    }

    /**
     * サムネイルキャッシュを取得する
     *
     * @param maxSize サムネイルの最大数
     * @param folderThumbnailOrder 取得するサムネイルの順番
     * @return
     */
    private suspend fun getThumbnailCacheList(
        maxSize: Int,
        folderThumbnailOrder: FolderThumbnailOrder,
    ): List<Pair<String, DiskCache.Snapshot>> {
        val cacheKeyList = fileModelLocalDataSource.getCacheKeys(
            folder.bookshelfId,
            folder.path,
            maxSize,
            FolderThumbnailOrder.valueOf(folderThumbnailOrder.name)
        )
        val notEnough = cacheKeyList.size < maxSize
        val list = cacheKeyList.mapNotNull { cacheKey ->
            diskCache?.openSnapshot(cacheKey)?.let {
                cacheKey to it
            } ?: null.apply {
                fileModelLocalDataSource.removeCacheKey(cacheKey)
            }
        }
        return if (notEnough || maxSize <= list.size) {
            list.reversed()
        } else {
            getThumbnailCacheList(maxSize, folderThumbnailOrder)
        }
    }

    /**
     * サムネイルの最大サイズを計算する
     *
     * @param snapshotList 本のサムネイルのスナップショット
     * @return 最大サイズ
     */
    private fun calculateMaxThumbnailSize(snapshotList: List<Pair<String, DiskCache.Snapshot>>): PxSize {
        val step = floor(requestHeight / 12).toInt()
        var rightPadding = 0
        var maxWidth = 0
        var maxHeight = 0
        snapshotList.forEach {
            val options = BitmapFactory.Options().also { options ->
                options.inJustDecodeBounds = true
                BitmapFactory.decodeFile(it.second.data.toString(), options)
                options.inSampleSize =
                    calculateInSampleSize(options, requestHeight.toInt(), requestWidth.toInt())
                BitmapFactory.decodeFile(it.second.data.toString(), options)
            }
            val resizeScale =
                if (options.outWidth >= options.outHeight) {
                    requestWidth / options.outWidth
                } else {
                    requestHeight / options.outHeight
                }
            maxWidth = max(maxWidth, rightPadding + (options.outWidth * resizeScale).toInt())
            maxHeight = max(maxHeight, (options.outHeight * resizeScale).toInt())
            rightPadding += step
        }
        return maxWidth to maxHeight
    }

    /**
     * フォルダーのサムネイル(Bitmap)を作成する
     *
     * @param snapshotList 本のサムネイルのリスト
     * @param size サムネイルのサイズ
     * @return 作成したBitmap
     */
    private fun createFolderThumbnailBitmap(
        snapshotList: List<Pair<String, DiskCache.Snapshot>>,
        size: PxSize,
    ): Bitmap {
        val step = floor(requestHeight / 12).toInt()
        val result = Bitmap.createBitmap(size.first, size.second, Bitmap.Config.RGB_565)
        val canvas = Canvas(result)
        canvas.drawColor(Color.TRANSPARENT)
        snapshotList.forEachIndexed { index, snapshot ->
            snapshot.second.use { snap ->
                combineThumbnail(canvas, snap, step * index)
            }
        }
        return result
    }

    /**
     * Combine thumbnail
     *
     * @param canvas
     * @param snap
     * @param paddingRight
     */
    private fun combineThumbnail(canvas: Canvas, snap: DiskCache.Snapshot, paddingRight: Int) {
        val bitmap = decodeBitmapFromSnapshotWithSample(snap, canvas.width, canvas.height)
        val resizeScale = min(
            (canvas.width - paddingRight) / bitmap.width.toFloat(),
            canvas.height / bitmap.height.toFloat()
        )
        val scale = bitmap.scale(
            (bitmap.width * resizeScale).toInt(),
            (bitmap.height * resizeScale).toInt(),
            true
        )
        bitmap.recycle()
        canvas.drawBitmap(
            scale,
            (canvas.width - scale.width - paddingRight).toFloat(),
            (canvas.height - scale.height).toFloat(),
            null
        )
        scale.recycle()
    }

    /**
     * スナップショットをビットマップにデコードします。
     *
     * @param snapshot デコードするファイルのスナップショット
     * @param reqWidth 要求するBitmapの幅
     * @param reqHeight 要求するBitmapの高さ
     */
    private fun decodeBitmapFromSnapshotWithSample(
        snapshot: DiskCache.Snapshot,
        reqWidth: Int,
        reqHeight: Int,
    ) =
        BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(snapshot.data.toString(), this)
            inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)
            inJustDecodeBounds = false
            BitmapFactory.decodeFile(snapshot.data.toString(), this)
        }

    /**
     * サンプリングサイズを計算する
     *
     * @param options BitmapFactory.Options
     * @param reqWidth 要求するBitmapの幅
     * @param reqHeight 要求するBitmapの高さ
     * @param moreSample 更にサンプリングするかどうか
     * @return
     */
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int,
        moreSample: Boolean = true,
    ): Int {
        val (height, width) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        if (moreSample) {
            inSampleSize *= 2
        }

        return inSampleSize
    }

    class Factory @Inject constructor(
        @ApplicationContext private val context: Context,
        private val fileModelLocalDataSource: FileLocalDataSource,
        private val datastoreDataSource: DatastoreDataSource,
    ) : Fetcher.Factory<Folder> {

        override fun create(data: Folder, options: Options, imageLoader: ImageLoader): Fetcher {
            return FolderThumbnailFetcher(
                data,
                options,
                { CoilDiskCache.thumbnailDiskCache(context, data.bookshelfId) },
                fileModelLocalDataSource,
                datastoreDataSource
            )
        }

    }
}
