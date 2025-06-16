package com.sorrowblue.comicviewer.framework.designsystem.icon.symbols

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

val ComicIcons.BottomNavigation: ImageVector
    get() {
        if (_bottomNavigation != null) {
            return _bottomNavigation!!
        }
        _bottomNavigation = Builder(
            name = "BottomNavigation", defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(200.0f, 840.0f)
                quadTo(167.0f, 840.0f, 143.5f, 816.5f)
                quadTo(120.0f, 793.0f, 120.0f, 760.0f)
                lineTo(120.0f, 200.0f)
                quadTo(120.0f, 167.0f, 143.5f, 143.5f)
                quadTo(167.0f, 120.0f, 200.0f, 120.0f)
                lineTo(760.0f, 120.0f)
                quadTo(793.0f, 120.0f, 816.5f, 143.5f)
                quadTo(840.0f, 167.0f, 840.0f, 200.0f)
                lineTo(840.0f, 760.0f)
                quadTo(840.0f, 793.0f, 816.5f, 816.5f)
                quadTo(793.0f, 840.0f, 760.0f, 840.0f)
                lineTo(200.0f, 840.0f)
                close()
                moveTo(200.0f, 600.0f)
                lineTo(760.0f, 600.0f)
                lineTo(760.0f, 200.0f)
                quadTo(760.0f, 200.0f, 760.0f, 200.0f)
                quadTo(760.0f, 200.0f, 760.0f, 200.0f)
                lineTo(200.0f, 200.0f)
                quadTo(200.0f, 200.0f, 200.0f, 200.0f)
                quadTo(200.0f, 200.0f, 200.0f, 200.0f)
                lineTo(200.0f, 600.0f)
                close()
                moveTo(200.0f, 680.0f)
                lineTo(200.0f, 760.0f)
                quadTo(200.0f, 760.0f, 200.0f, 760.0f)
                quadTo(200.0f, 760.0f, 200.0f, 760.0f)
                lineTo(760.0f, 760.0f)
                quadTo(760.0f, 760.0f, 760.0f, 760.0f)
                quadTo(760.0f, 760.0f, 760.0f, 760.0f)
                lineTo(760.0f, 680.0f)
                lineTo(200.0f, 680.0f)
                close()
                moveTo(200.0f, 680.0f)
                lineTo(200.0f, 680.0f)
                lineTo(200.0f, 760.0f)
                quadTo(200.0f, 760.0f, 200.0f, 760.0f)
                quadTo(200.0f, 760.0f, 200.0f, 760.0f)
                lineTo(200.0f, 760.0f)
                quadTo(200.0f, 760.0f, 200.0f, 760.0f)
                quadTo(200.0f, 760.0f, 200.0f, 760.0f)
                lineTo(200.0f, 680.0f)
                close()
            }
        }
            .build()
        return _bottomNavigation!!
    }

private var _bottomNavigation: ImageVector? = null
