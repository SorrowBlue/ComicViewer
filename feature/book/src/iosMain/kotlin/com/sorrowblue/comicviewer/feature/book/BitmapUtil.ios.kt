package com.sorrowblue.comicviewer.feature.book

import androidx.compose.ui.graphics.Color
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Image
import org.jetbrains.skia.Rect

internal actual fun coil3.Bitmap.trimBorders(color: Color): coil3.Bitmap {
    val width = width
    val height = height

    var top = 0
    var bottom = height - 1
    var left = 0
    var right = width - 1

    // 白色を定義 (ARGB: 0xFFFFFFFF)
    val white = -0x1 // Int表現の 0xFFFFFFFF

    // 上端を探す
    findTop@ for (y in 0 until height) {
        for (x in 0 until width) {
            if (getColor(x, y) != white) {
                top = y
                break@findTop
            }
        }
        if (y == height - 1) return this // すべて白の場合
    }

    // 下端を探す
    findBottom@ for (y in height - 1 downTo top) {
        for (x in 0 until width) {
            if (getColor(x, y) != white) {
                bottom = y
                break@findBottom
            }
        }
    }

    // 左端を探す
    findLeft@ for (x in 0 until width) {
        for (y in top..bottom) {
            if (getColor(x, y) != white) {
                left = x
                break@findLeft
            }
        }
    }

    // 右端を探す
    findRight@ for (x in width - 1 downTo left) {
        for (y in top..bottom) {
            if (getColor(x, y) != white) {
                right = x
                break@findRight
            }
        }
    }

    val newWidth = right - left + 1
    val newHeight = bottom - top + 1

    // 切り抜いたサイズの新しいBitmapを作成
    val result = Bitmap()
    result.allocN32Pixels(newWidth, newHeight)

    val canvas = Canvas(result)
    // 元の画像をオフセット（ずらして）描画することで切り抜きを実現
    canvas.drawImageRect(
        Image.makeFromBitmap(this),
        Rect.makeLTRB(left.toFloat(), top.toFloat(), (right + 1).toFloat(), (bottom + 1).toFloat()),
        Rect.makeWH(newWidth.toFloat(), newHeight.toFloat()),
    )
    canvas.close()

    return result
}
