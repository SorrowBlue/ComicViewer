package com.sorrowblue.comicviewer.data.coil

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import androidx.core.graphics.scale
import coil3.decode.DecodeUtils
import coil3.disk.DiskCache
import coil3.size.Scale
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import logcat.logcat
import okio.use

typealias PxSize = Pair<Int, Int>
typealias CacheKeySnapshot = Pair<String, DiskCache.Snapshot>

object CoilDecoder {

    /**
     * サムネイルの最大サイズを計算する
     *
     * @param snapshotList 本のサムネイルのスナップショット
     * @return 最大サイズ
     */
    fun calculateMaxThumbnailSize(
        snapshotList: List<CacheKeySnapshot>,
        dstWidth: Float,
        dstHeight: Float,
    ): PxSize {
        val step = floor(dstHeight / 12f).toInt()
        var rightPadding = 0
        var maxWidth = 0
        var maxHeight = 0
        snapshotList.forEach {
            val options = BitmapFactory.Options().also { options ->
                options.inJustDecodeBounds = true
                BitmapFactory.decodeFile(it.second.data.toString(), options)
                options.inSampleSize = DecodeUtils.calculateInSampleSize(
                    srcWidth = options.outWidth,
                    srcHeight = options.outHeight,
                    dstWidth = dstWidth.toInt(),
                    dstHeight = dstHeight.toInt(),
                    scale = Scale.FIT
                )
                BitmapFactory.decodeFile(it.second.data.toString(), options)
            }
            val resizeScale =
                if (options.outWidth >= options.outHeight) dstWidth / options.outWidth else dstHeight / options.outHeight
            maxWidth = max(maxWidth, rightPadding + (options.outWidth * resizeScale).toInt())
            maxHeight = max(maxHeight, (options.outHeight * resizeScale).toInt())
            rightPadding += step
        }
        return maxWidth to maxHeight
    }

    /**
     * スナップショットを一つづつ左にずらしたBitmapを作成する
     *
     * @param thumbnailSnapshots サムネイルのスナップショットリスト
     * @param outSize サムネイルのサイズ
     * @return 作成したBitmap
     */
    fun createShiftedBitmapFromSnapshots(
        thumbnailSnapshots: List<CacheKeySnapshot>,
        outSize: PxSize,
        shiftSize: Int = floor(outSize.first / 12f).toInt(),
    ): Bitmap? {
        logcat { "createShiftedBitmapFromSnapshots($thumbnailSnapshots, $outSize, $shiftSize)" }
        val outBitmap = Bitmap.createBitmap(outSize.first, outSize.second, Bitmap.Config.RGB_565)
        val canvas = Canvas(outBitmap)
        canvas.drawColor(Color.TRANSPARENT)
        thumbnailSnapshots.forEachIndexed { index, snapshot ->
            snapshot.second.use { snap ->
                val result = canvas.drawShiftedSnapshot(snap, shiftSize * index)
                if (!result) {
                    return null
                }
            }
        }
        return outBitmap
    }

    /**
     * Draw shifted snapshot
     *
     * @param snap
     * @param paddingRight
     * @return
     */
    private fun Canvas.drawShiftedSnapshot(
        snap: DiskCache.Snapshot,
        paddingRight: Int,
    ): Boolean {
        val bitmap =
            decodeBitmapFromSnapshotWithSampleSize(snap, width, height)
                ?: return false
        val resizeScale = min(
            (width - paddingRight) / bitmap.width.toFloat(),
            height / bitmap.height.toFloat()
        )
        val scale = bitmap.scale(
            (bitmap.width * resizeScale).toInt(),
            (bitmap.height * resizeScale).toInt(),
            true
        )
        bitmap.recycle()
        drawBitmap(
            scale,
            (width - scale.width - paddingRight).toFloat(),
            (height - scale.height).toFloat(),
            null
        )
        scale.recycle()
        return true
    }

    /**
     * スナップショットをビットマップにデコードします。
     *
     * @param snapshot デコードするファイルのスナップショット
     * @param dstWidth 要求するBitmapの幅
     * @param dstHeight 要求するBitmapの高さ
     */
    private fun decodeBitmapFromSnapshotWithSampleSize(
        snapshot: DiskCache.Snapshot,
        dstWidth: Int,
        dstHeight: Int,
    ): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(snapshot.data.toString(), options)
        options.inSampleSize = DecodeUtils.calculateInSampleSize(
            srcWidth = options.outWidth,
            srcHeight = options.outHeight,
            dstWidth = dstWidth,
            dstHeight = dstHeight,
            scale = Scale.FIT
        )
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(snapshot.data.toString(), options)
    }

}
