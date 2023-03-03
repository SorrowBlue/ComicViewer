package com.sorrowblue.comicviewer.data.coil

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import androidx.core.graphics.scale
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.decode.DataSource
import coil.disk.DiskCache
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.request.Options
import coil.size.Dimension
import com.sorrowblue.comicviewer.data.coil.folder.FolderThumbnailMetadata
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteModel
import com.sorrowblue.comicviewer.data.datasource.FavoriteBookLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.FileModelLocalDataSource
import com.sorrowblue.comicviewer.data.di.ThumbnailDiskCache
import java.io.IOException
import javax.inject.Inject
import kotlin.math.floor
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import logcat.logcat
import okhttp3.internal.closeQuietly
import okio.ByteString.Companion.encodeUtf8

@OptIn(ExperimentalCoilApi::class)
internal class FavoriteThumbnailFetcher(
    private val data: FavoriteModel,
    private val options: Options,
    diskCacheLazy: dagger.Lazy<DiskCache?>,
    private val favoriteBookLocalDataSource: FavoriteBookLocalDataSource,
    private val fileModelLocalDataSource: FileModelLocalDataSource,
) : CoilFetcher<FavoriteModel>(data, options, diskCacheLazy) {

    class Factory @Inject constructor(
        @ThumbnailDiskCache private val diskCache: dagger.Lazy<DiskCache?>,
        private val favoriteBookLocalDataSource: FavoriteBookLocalDataSource,
        private val fileModelLocalDataSource: FileModelLocalDataSource
    ) : Fetcher.Factory<FavoriteModel> {

        override fun create(
            data: FavoriteModel,
            options: Options,
            imageLoader: ImageLoader
        ): Fetcher {
            return FavoriteThumbnailFetcher(
                data,
                options,
                diskCache,
                favoriteBookLocalDataSource,
                fileModelLocalDataSource
            )
        }
    }

    private val width = (options.size.width as? Dimension.Pixels)?.px?.toFloat() ?: 300f
    private val height = (options.size.height as? Dimension.Pixels)?.px?.toFloat() ?: 300f

    override suspend fun fetch(): FetchResult {
        logcat { "fetch() data=${data}" }
        val snapshot = readFromDiskCache()
        return fetchFolder(snapshot)
    }

    private suspend fun fetchFolder(snapshot: DiskCache.Snapshot?): FetchResult {
        val size = (width / (height / 11).toInt()).toInt() - 6
        if (snapshot != null) {
            // キャッシュされた画像は手動で追加された可能性が高いため、常にメタデータが空の状態で返されます。
            if (fileSystem.metadata(snapshot.metadata).size == 0L) {
                return SourceResult(
                    source = snapshot.toImageSource(),
                    mimeType = null,
                    dataSource = DataSource.DISK
                )
            }
            // 作成済みの場合
            // サムネイル候補キャッシュを取得
            val list = favoriteBookLocalDataSource.getCacheKeyList(data.id, size)
            // 候補が適格である場合、キャッシュから候補を返します。
            if (snapshot.toFolderThumbnailMetadata()?.thumbnails == list) {
                // 最新の場合
                return SourceResult(
                    source = snapshot.toImageSource(),
                    mimeType = null,
                    dataSource = DataSource.DISK
                )
            }
        }
        try {
            // 未作成・古い場合、サムネイルキャッシュから取得する
            val list = cacheList(size)
            val snapshot1 = writeToDiskCacheFolder(snapshot, list)
            return if (snapshot1 != null) {
                SourceResult(
                    source = snapshot1.toImageSource(),
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
        } catch (e: Exception) {
            snapshot?.closeQuietly()
            e.printStackTrace()
            throw e
        }
    }

    private fun DiskCache.Snapshot.toFolderThumbnailMetadata(): FolderThumbnailMetadata? {
        return try {
            fileSystem.read(metadata) {
                use(FolderThumbnailMetadata.Companion::from)
            }
        } catch (_: IOException) {
            // If we can't parse the metadata, ignore this entry.
            null
        }
    }

    private suspend fun cacheList(size: Int): List<Pair<String, DiskCache.Snapshot>> {
        val cacheKeyList = favoriteBookLocalDataSource.getCacheKeyList(data.id, size)
        val notEnough = cacheKeyList.size < size
        val list = cacheKeyList.mapNotNull { cacheKey ->
            diskCache?.get(cacheKey)?.let {
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

    private suspend fun writeToDiskCacheFolder(
        snapshot: DiskCache.Snapshot?,
        list: List<Pair<String, DiskCache.Snapshot>>
    ): DiskCache.Snapshot? {
        // Open a new editor.
        val editor = if (snapshot != null) {
            snapshot.closeAndEdit()
        } else {
            diskCache?.edit(diskCacheKey)
        }
        // Return `null` if we're unable to write to this entry.
        if (editor == null) return null

        val result = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        canvas.drawColor(Color.TRANSPARENT)
        val step = floor(height / 12)
        var left = 0f
        list.reversed().forEach {
            it.second.use { snap ->
                combineThumbnail(canvas, snap, left)
                left += step
            }
        }
        return withContext(NonCancellable) {
            fileSystem.write(editor.metadata) {
                FolderThumbnailMetadata("", 0, 0, list.map { it.first }).writeTo(this)
            }
            fileSystem.write(editor.data) {
                outputStream().use {
                    result.compress(COMPRESS_FORMAT, 75, it)
                }
                result.recycle()
            }
            editor.commitAndGet()
        }
    }

    private fun combineThumbnail(canvas: Canvas, snap: DiskCache.Snapshot, rightSpace: Float) {
        val bitmap = BitmapFactory.decodeFile(snap.data.toString())
        val resizeScale =
            if (bitmap.width >= bitmap.height) width / bitmap.width else height / bitmap.height
        val scale = bitmap.scale(
            (bitmap.width * resizeScale).toInt(),
            (bitmap.height * resizeScale).toInt(),
            true
        )
        bitmap.recycle()
        canvas.drawBitmap(scale, width - scale.width - rightSpace, 0f, null)
        scale.recycle()
    }

    override val diskCacheKey
        get() = options.diskCacheKey ?: "favorite_id=${data.id.value}&thumbnail".encodeUtf8()
            .sha256().hex()

    companion object {
        private val COMPRESS_FORMAT =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Bitmap.CompressFormat.WEBP_LOSSY else Bitmap.CompressFormat.JPEG
    }
}
