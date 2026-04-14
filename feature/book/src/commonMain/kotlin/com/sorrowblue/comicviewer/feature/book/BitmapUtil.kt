package com.sorrowblue.comicviewer.feature.book

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import coil3.Bitmap

internal expect fun Bitmap.createSplitBitmap(isLeft: Boolean): Bitmap

internal expect fun Bitmap.asImageBitmap(): ImageBitmap
internal expect fun Bitmap.trimBorders(color: Color): Bitmap

internal expect val Bitmap.imageWidth: Int
internal expect val Bitmap.imageHeight: Int
