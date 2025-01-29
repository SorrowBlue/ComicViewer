package com.sorrowblue.comicviewer.feature.book.section

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import coil3.Bitmap
import org.jetbrains.skia.IRect

internal actual fun org.jetbrains.skia.Bitmap.createSplitBitmap(isLeft: Boolean): org.jetbrains.skia.Bitmap {
    val original = this
    val width = original.width
    val height = original.height
    val halfWidth = width / 2

    val leftHalfRect = IRect.makeLTRB(0, 0, halfWidth, height)
    val leftHalfBitmap = org.jetbrains.skia.Bitmap()
    original.extractSubset(leftHalfBitmap, leftHalfRect)
    return leftHalfBitmap
}

internal actual fun Bitmap.asImageBitmap(): ImageBitmap {
    return asComposeImageBitmap()
}

internal actual val Bitmap.width2: Int
    get() = width

internal actual val Bitmap.height2: Int
    get() = height
