@file:Suppress("detekt.all")

package com.sorrowblue.comicviewer.framework.designsystem.icon.composeicons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

val ComicIcons.FilePdf: ImageVector
    get() {
        if (_FilePdf != null) return _FilePdf!!

        _FilePdf = ImageVector.Builder(
            name = "FilePdf",
            defaultWidth = 16.dp,
            defaultHeight = 16.dp,
            viewportWidth = 16f,
            viewportHeight = 16f,
        ).apply {
            path(
                fill = SolidColor(Color.Black),
            ) {
                moveTo(13.85f, 4.44f)
                lineToRelative(-3.28f, -3.3f)
                lineToRelative(-0.35f, -0.14f)
                horizontalLineTo(2.5f)
                lineToRelative(-0.5f, 0.5f)
                verticalLineTo(7f)
                horizontalLineToRelative(1f)
                verticalLineTo(2f)
                horizontalLineToRelative(6f)
                verticalLineToRelative(3.5f)
                lineToRelative(0.5f, 0.5f)
                horizontalLineTo(13f)
                verticalLineToRelative(1f)
                horizontalLineToRelative(1f)
                verticalLineTo(4.8f)
                lineToRelative(-0.15f, -0.36f)
                close()
                moveTo(10f, 5f)
                verticalLineTo(2f)
                lineToRelative(3f, 3f)
                horizontalLineToRelative(-3f)
                close()
                moveTo(2.5f, 8f)
                lineToRelative(-0.5f, 0.5f)
                verticalLineToRelative(6f)
                lineToRelative(0.5f, 0.5f)
                horizontalLineToRelative(11f)
                lineToRelative(0.5f, -0.5f)
                verticalLineToRelative(-6f)
                lineToRelative(-0.5f, -0.5f)
                horizontalLineToRelative(-11f)
                close()
                moveTo(13f, 13f)
                verticalLineToRelative(1f)
                horizontalLineTo(3f)
                verticalLineTo(9f)
                horizontalLineToRelative(10f)
                verticalLineToRelative(4f)
                close()
                moveToRelative(-8f, -1f)
                horizontalLineToRelative(-0.32f)
                verticalLineToRelative(1f)
                horizontalLineTo(4f)
                verticalLineToRelative(-3f)
                horizontalLineToRelative(1.06f)
                curveToRelative(0.75f, 0f, 1.13f, 0.36f, 1.13f, 1f)
                arcToRelative(0.94f, 0.94f, 0f, false, true, -0.32f, 0.72f)
                arcTo(1.33f, 1.33f, 0f, false, true, 5f, 12f)
                close()
                moveToRelative(-0.06f, -1.45f)
                horizontalLineToRelative(-0.26f)
                verticalLineToRelative(0.93f)
                horizontalLineToRelative(0.26f)
                curveToRelative(0.36f, 0f, 0.54f, -0.16f, 0.54f, -0.47f)
                curveToRelative(0f, -0.31f, -0.18f, -0.46f, -0.54f, -0.46f)
                close()
                moveTo(9f, 12.58f)
                arcToRelative(1.48f, 1.48f, 0f, false, false, 0.44f, -1.12f)
                curveToRelative(0f, -1f, -0.53f, -1.46f, -1.6f, -1.46f)
                horizontalLineTo(6.78f)
                verticalLineToRelative(3f)
                horizontalLineToRelative(1.06f)
                arcTo(1.6f, 1.6f, 0f, false, false, 9f, 12.58f)
                close()
                moveToRelative(-1.55f, -0.13f)
                verticalLineToRelative(-1.9f)
                horizontalLineToRelative(0.33f)
                arcToRelative(0.94f, 0.94f, 0f, false, true, 0.7f, 0.25f)
                arcToRelative(0.91f, 0.91f, 0f, false, true, 0.25f, 0.67f)
                arcToRelative(1f, 1f, 0f, false, true, -0.25f, 0.72f)
                arcToRelative(0.94f, 0.94f, 0f, false, true, -0.69f, 0.26f)
                horizontalLineToRelative(-0.34f)
                close()
                moveToRelative(4.45f, -0.61f)
                horizontalLineToRelative(-0.97f)
                verticalLineTo(13f)
                horizontalLineToRelative(-0.68f)
                verticalLineToRelative(-3f)
                horizontalLineToRelative(1.74f)
                verticalLineToRelative(0.55f)
                horizontalLineToRelative(-1.06f)
                verticalLineToRelative(0.74f)
                horizontalLineToRelative(0.97f)
                verticalLineToRelative(0.55f)
                close()
            }
        }.build()

        return _FilePdf!!
    }

private var _FilePdf: ImageVector? = null
