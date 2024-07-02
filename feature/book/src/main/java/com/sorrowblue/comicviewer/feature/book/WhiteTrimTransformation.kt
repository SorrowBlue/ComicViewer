package com.sorrowblue.comicviewer.feature.book

import android.graphics.Bitmap
import android.graphics.Color
import coil3.size.Size
import coil3.transform.Transformation

object WhiteTrimTransformation : Transformation() {

    override val cacheKey = "${this::class.qualifiedName}"

    override suspend fun transform(input: Bitmap, size: Size) =
        input.trimBorders(Color.WHITE)
}

fun Bitmap.trimBorders(color: Int, maxMargin: Int = 0): Bitmap {
    val bitmapArray = IntArray(width * height)
    getPixels(bitmapArray, 0, width, 0, 0, width, height)

    // Find first non-color pixel from top of bitmap
    var maxTop = 0
    searchTopMargin@ for (y in 0 until height) {
        for (x in 0 until width) {
            if (bitmapArray[width * y + x] != color) {
                maxTop = if (y > maxMargin) y - maxMargin else 0
                break@searchTopMargin
            }
        }
    }

    // Find first non-color pixel from bottom of bitmap
    var maxBottom = 0
    searchBottomMargin@ for (y in height - 1 downTo 0) {
        for (x in width - 1 downTo 0) {
            if (bitmapArray[width * y + x] != color) {
                maxBottom = if (y < height - maxMargin) y + maxMargin else height
                break@searchBottomMargin
            }
        }
    }

    // Find first non-color pixel from left of bitmap
    var maxLeft = 0
    searchLeftMargin@ for (x in 0 until width) {
        for (y in 0 until height) {
            if (bitmapArray[width * y + x] != color) {
                maxLeft = if (x > maxMargin) x - maxMargin else 0
                break@searchLeftMargin
            }
        }
    }

    // Find first non-color pixel from right of bitmap
    var maxRight = 0
    searchRightMargin@ for (x in width - 1 downTo 0) {
        for (y in height - 1 downTo 0) {
            if (bitmapArray[width * y + x] != color) {
                maxRight = if (x < width - maxMargin) x + maxMargin else width
                break@searchRightMargin
            }
        }
    }

    return Bitmap.createBitmap(this, maxLeft, maxTop, maxRight - maxLeft, maxBottom - maxTop)
}
