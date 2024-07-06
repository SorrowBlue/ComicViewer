package com.sorrowblue.comicviewer.data.coil.favorite

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
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.service.datasource.FavoriteFileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import dagger.Lazy
import javax.inject.Inject
import kotlin.math.floor
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import okio.BufferedSource
import okio.ByteString.Companion.encodeUtf8

internal class FavoriteThumbnailFetcher(
    options: Options,
    diskCache: Lazy<DiskCache?>,
    private val data: Favorite,
    private val favoriteFileLocalDataSource: FavoriteFileLocalDataSource,
    private val fileModelLocalDataSource: FileLocalDataSource,
) : FileFetcher<FavoriteThumbnailMetadata>(options, diskCache) {

    override suspend fun innerFetch(snapshot: DiskCache.Snapshot?): FetchResult {
        val size = (requestWidth / (requestHeight / 11).toInt()).toInt() - 6
        val thumbnails = cacheList(size)
        if (thumbnails.isEmpty()) {
            // キャッシュがない場合、取得しない。
            throw CoilRuntimeException("ファイルのサムネイルがないので、サムネイルを生成しない。")
        } else {
            // 応答をディスク キャッシュに書き込み、新しいスナップショットを開きます。
            return writeToDiskCache(snapshot = snapshot, list = thumbnails)?.use {
                SourceFetchResult(
                    source = it.toImageSource(),
                    mimeType = null,
                    dataSource = DataSource.DISK
                )
            } ?: run {
                throw CoilRuntimeException("フォルダのサムネイル生成失敗。")
            }
        }
    }

    override val diskCacheKey
        get() = options.diskCacheKey ?: "${data.id.value}".encodeUtf8().sha256().hex()

    override fun BufferedSource.readMetadata() =
        FavoriteThumbnailMetadata.from<FavoriteThumbnailMetadata>(this)

    override suspend fun metadata(): FavoriteThumbnailMetadata {
        val size = (requestWidth / (requestHeight / 11).toInt()).toInt() - 6
        val thumbnails = favoriteFileLocalDataSource.getCacheKeyList(data.id, size)
        return FavoriteThumbnailMetadata(data.id.value, thumbnails)
    }

    private suspend fun cacheList(size: Int): List<Pair<String, DiskCache.Snapshot>> {
        val cacheKeyList = favoriteFileLocalDataSource.getCacheKeyList(data.id, size)
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
            cacheList(size)
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
                    FavoriteThumbnailMetadata(data.id.value, list.map { it.first }).writeTo(this)
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
        @ThumbnailDiskCache private val diskCache: Lazy<DiskCache?>,
        private val favoriteFileLocalDataSource: FavoriteFileLocalDataSource,
        private val fileModelLocalDataSource: FileLocalDataSource,
    ) : Fetcher.Factory<Favorite> {

        override fun create(data: Favorite, options: Options, imageLoader: ImageLoader) =
            FavoriteThumbnailFetcher(
                options,
                diskCache,
                data,
                favoriteFileLocalDataSource,
                fileModelLocalDataSource
            )
    }
}
