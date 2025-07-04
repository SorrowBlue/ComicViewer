package com.sorrowblue.comicviewer.feature.book

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

internal actual fun Bitmap.createSplitBitmap(isLeft: Boolean): Bitmap {
    return Bitmap.createBitmap(
        this,
        if (isLeft) 0 else this.width / 2,
        0,
        this.width / 2,
        this.height
    ).apply {
        this@createSplitBitmap.recycle()
    }
}

internal actual fun Bitmap.asImageBitmap(): ImageBitmap {
    return asImageBitmap()
}

internal actual val Bitmap.imageWidth: Int
    get() = width

internal actual val Bitmap.imageHeight: Int
    get() = height
