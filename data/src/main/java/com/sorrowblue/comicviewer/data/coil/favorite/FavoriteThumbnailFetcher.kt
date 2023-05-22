package com.sorrowblue.comicviewer.data.coil.favorite

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import androidx.core.graphics.scale
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.decode.DataSource
import coil.disk.DiskCache
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.request.Options
import com.sorrowblue.comicviewer.data.coil.abortQuietly
import com.sorrowblue.comicviewer.data.coil.book.FileModelFetcher
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteModel
import com.sorrowblue.comicviewer.data.datasource.FavoriteBookLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.FileModelLocalDataSource
import com.sorrowblue.comicviewer.data.di.ThumbnailDiskCache
import java.io.IOException
import javax.inject.Inject
import kotlin.math.floor
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import okhttp3.internal.closeQuietly
import okio.ByteString.Companion.encodeUtf8

@OptIn(ExperimentalCoilApi::class)
internal class FavoriteThumbnailFetcher(
    private val data: FavoriteModel,
    options: Options,
    diskCache: dagger.Lazy<DiskCache?>,
    private val favoriteBookLocalDataSource: FavoriteBookLocalDataSource,
    private val fileModelLocalDataSource: FileModelLocalDataSource,
) : FileModelFetcher(options, diskCache) {

    override suspend fun fetch(): FetchResult {
        val size = (requestWidth / (requestHeight / 11).toInt()).toInt() - 6
        var snapshot = readFromDiskCache()
        try {
            if (snapshot != null) {
                // キャッシュされた画像は手動で追加された可能性が高いため、常にメタデータが空の状態で返されます。
                if (fileSystem.metadata(snapshot.metadata).size == 0L) {
                    return SourceResult(
                        source = snapshot.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.DISK
                    )
                }
                // サムネイル候補キャッシュを取得
                val thumbnails = favoriteBookLocalDataSource.getCacheKeyList(data.id, size)
                // 候補が適格である場合、キャッシュから候補を返します。
                if (snapshot.toFavoriteThumbnailMetadata() == FavoriteThumbnailMetadata(
                        data.id.value, thumbnails
                    )
                ) {
                    return SourceResult(
                        source = snapshot.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.DISK
                    )
                }
            }
            try {
                val thumbnails = cacheList(size)
                if (thumbnails.isEmpty()) {
                    // キャッシュがない場合、取得しない。
                    throw RuntimeException("ファイルのサムネイルがないので、サムネイルを生成しない。")
                } else {
                    // 応答をディスク キャッシュに書き込み、新しいスナップショットを開きます。
                    snapshot = writeToDiskCache(snapshot = snapshot, list = thumbnails)
                    return if (snapshot != null) {
                        SourceResult(
                            source = snapshot.toImageSource(),
                            mimeType = null,
                            dataSource = DataSource.DISK
                        )
                    } else {
                        SourceResult(
                            source = readFromDiskCache()!!.toImageSource(),
                            mimeType = null,
                            dataSource = DataSource.DISK
                        )
                    }
                }
            } catch (e: Exception) {
                throw e
            }
        } catch (e: Exception) {
            snapshot?.closeQuietly()
            throw e
        }
    }

    private suspend fun cacheList(size: Int): List<Pair<String, DiskCache.Snapshot>> {
        val cacheKeyList = favoriteBookLocalDataSource.getCacheKeyList(data.id, size)
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
        snapshot: DiskCache.Snapshot?, list: List<Pair<String, DiskCache.Snapshot>>
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
                requestWidth.toInt(), requestHeight.toInt(), Bitmap.Config.ARGB_8888
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
            (bitmap.width * resizeScale).toInt(), (bitmap.height * resizeScale).toInt(), true
        )
        bitmap.recycle()
        canvas.drawBitmap(scale, requestWidth - scale.width - rightSpace, 0f, null)
        scale.recycle()
    }

    override val diskCacheKey
        get() = options.diskCacheKey ?: "${data.id.value}".encodeUtf8().sha256().hex()

    private fun DiskCache.Snapshot.toFavoriteThumbnailMetadata(): FavoriteThumbnailMetadata? {
        return try {
            fileSystem.read(metadata) {
                use(FavoriteThumbnailMetadata::from)
            }
        } catch (_: IOException) {
            // If we can't parse the metadata, ignore this entry.
            null
        }
    }

    class Factory @Inject constructor(
        @ThumbnailDiskCache private val diskCache: dagger.Lazy<DiskCache?>,
        private val favoriteBookLocalDataSource: FavoriteBookLocalDataSource,
        private val fileModelLocalDataSource: FileModelLocalDataSource
    ) : Fetcher.Factory<FavoriteModel> {

        override fun create(data: FavoriteModel, options: Options, imageLoader: ImageLoader) =
            FavoriteThumbnailFetcher(
                data,
                options,
                diskCache,
                favoriteBookLocalDataSource,
                fileModelLocalDataSource
            )
    }
}
