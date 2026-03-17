package com.sorrowblue.comicviewer.framework.ui.preview

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import coil3.Canvas
import coil3.Image
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.ui.R

@OptIn(ExperimentalCoilApi::class)
internal actual val provideAsyncImagePreviewHandler: ProvidedValue<AsyncImagePreviewHandler>
    @Composable
    get() {
        val context = LocalContext.current
        val previewHandler = AsyncImagePreviewHandler { PreviewImage(context) }
        return LocalAsyncImagePreviewHandler provides previewHandler
    }

internal actual class PreviewImage(context: PlatformContext) : Image {
    private val bitmap =
        requireNotNull(ContextCompat.getDrawable(context, nextSampleAvatar)).toBitmap()

    actual override val shareable = false

    actual override val size
        get() = bitmap.allocationByteCountCompat.toLong()

    actual override val width
        get() = bitmap.width

    actual override val height
        get() = bitmap.height

    actual override fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, 0f, 0f, null)
    }

    private val nextSampleAvatar
        get() = avatarList[avatarIndex++].apply {
            if (avatarIndex >= avatarList.size) {
                avatarIndex = 0
            }
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

private var avatarIndex = 0

private val avatarList = listOf(
    R.drawable.avatar_1,
    R.drawable.avatar_2,
    R.drawable.avatar_3,
    R.drawable.avatar_4,
    R.drawable.avatar_5,
    R.drawable.avatar_6,
    R.drawable.avatar_7,
    R.drawable.avatar_8,
    R.drawable.avatar_9,
    R.drawable.avatar_10,
    R.drawable.avatar_11,
    R.drawable.avatar_12,
    R.drawable.avatar_13,
    R.drawable.avatar_14,
    R.drawable.avatar_15,
    R.drawable.avatar_16,
)
