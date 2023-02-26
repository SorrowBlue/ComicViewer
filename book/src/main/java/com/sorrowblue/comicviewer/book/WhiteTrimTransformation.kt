package com.sorrowblue.comicviewer.book

import android.graphics.Bitmap
import android.graphics.Color
import coil.size.Size
import coil.transform.Transformation

class MihirakiSplitTransformation(private val isLeft: Boolean) : Transformation {
    override val cacheKey = "MihirakiSplitTransformation"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        return Bitmap.createBitmap(
            input,
            if (isLeft) 0 else input.width / 2,
            0,
            input.width / 2,
            input.height
        ).apply {
            input.recycle()
        }
    }

}

object WhiteTrimTransformation : Transformation {
    override val cacheKey = "WhiteTrimTransformation"
    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        return input.trimBorders(Color.WHITE)
    }

    private fun Bitmap.trimBorders(color: Int): Bitmap {
        var startX = 0
        loop@ for (x in 0 until width) {
            for (y in 0 until height) {
                if (getPixel(x, y) != color) {
                    startX = x
                    break@loop
                }
            }
        }
        var startY = 0
        loop@ for (y in 0 until height) {
            for (x in 0 until width) {
                if (getPixel(x, y) != color) {
                    startY = y
                    break@loop
                }
            }
        }
        var endX = width - 1
        loop@ for (x in endX downTo 0) {
            for (y in 0 until height) {
                if (getPixel(x, y) != color) {
                    endX = x
                    break@loop
                }
            }
        }
        var endY = height - 1
        loop@ for (y in endY downTo 0) {
            for (x in 0 until width) {
                if (getPixel(x, y) != color) {
                    endY = y
                    break@loop
                }
            }
        }

        val newWidth = endX - startX + 1
        val newHeight = endY - startY + 1

        return Bitmap.createBitmap(this, startX, startY, newWidth, newHeight)
    }
}
