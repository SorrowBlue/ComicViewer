package com.sorrowblue.comicviewer.framework.ui.preview

import android.content.Context
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import coil3.Image

internal class PreviewImage(context: Context) : Image {
    private val bitmap =
        ContextCompat.getDrawable(context, nextSampleAvatar)!!.toBitmap()

    override val shareable = false

    override val size
        get() = bitmap.allocationByteCountCompat.toLong()

    override val width
        get() = bitmap.width

    override val height
        get() = bitmap.height

    override fun draw(canvas: coil3.Canvas) {
        canvas.drawBitmap(bitmap, 0f, 0f, null)
    }

    private val Bitmap.allocationByteCountCompat: Int
        get() {
            check(!isRecycled) {
                "Cannot obtain size for recycled bitmap: $this [$width x $height] + $config"
            }

            return try {
                allocationByteCount
            } catch (_: Exception) {
                width * height * config.bytesPerPixel
            }
        }

    @Suppress("DEPRECATION")
    private val Bitmap.Config?.bytesPerPixel: Int
        get() = when {
            this == Bitmap.Config.ALPHA_8 -> 1
            this == Bitmap.Config.RGB_565 -> 2
            this == Bitmap.Config.ARGB_4444 -> 2
            this == Bitmap.Config.RGBA_F16 -> 8
            else -> 4
        }
}
