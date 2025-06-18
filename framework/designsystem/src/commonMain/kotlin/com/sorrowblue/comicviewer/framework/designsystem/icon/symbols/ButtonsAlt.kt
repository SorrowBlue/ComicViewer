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

val ComicIcons.ButtonsAlt: ImageVector
    get() {
        if (_buttonsAlt != null) {
            return _buttonsAlt!!
        }
        _buttonsAlt = Builder(
            name = "ButtonsAlt", defaultWidth = 24.0.dp,
            defaultHeight =
            24.0.dp,
            viewportWidth = 960.0f, viewportHeight = 960.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(160.0f, 720.0f)
                quadTo(127.0f, 720.0f, 103.5f, 696.5f)
                quadTo(80.0f, 673.0f, 80.0f, 640.0f)
                lineTo(80.0f, 320.0f)
                quadTo(80.0f, 287.0f, 103.5f, 263.5f)
                quadTo(127.0f, 240.0f, 160.0f, 240.0f)
                lineTo(800.0f, 240.0f)
                quadTo(833.0f, 240.0f, 856.5f, 263.5f)
                quadTo(880.0f, 287.0f, 880.0f, 320.0f)
                lineTo(880.0f, 640.0f)
                quadTo(880.0f, 673.0f, 856.5f, 696.5f)
                quadTo(833.0f, 720.0f, 800.0f, 720.0f)
                lineTo(160.0f, 720.0f)
                close()
                moveTo(160.0f, 640.0f)
                lineTo(800.0f, 640.0f)
                quadTo(800.0f, 640.0f, 800.0f, 640.0f)
                quadTo(800.0f, 640.0f, 800.0f, 640.0f)
                lineTo(800.0f, 320.0f)
                quadTo(800.0f, 320.0f, 800.0f, 320.0f)
                quadTo(800.0f, 320.0f, 800.0f, 320.0f)
                lineTo(160.0f, 320.0f)
                quadTo(160.0f, 320.0f, 160.0f, 320.0f)
                quadTo(160.0f, 320.0f, 160.0f, 320.0f)
                lineTo(160.0f, 640.0f)
                quadTo(160.0f, 640.0f, 160.0f, 640.0f)
                quadTo(160.0f, 640.0f, 160.0f, 640.0f)
                close()
                moveTo(290.0f, 600.0f)
                lineTo(350.0f, 600.0f)
                lineTo(350.0f, 510.0f)
                lineTo(440.0f, 510.0f)
                lineTo(440.0f, 450.0f)
                lineTo(350.0f, 450.0f)
                lineTo(350.0f, 360.0f)
                lineTo(290.0f, 360.0f)
                lineTo(290.0f, 450.0f)
                lineTo(200.0f, 450.0f)
                lineTo(200.0f, 510.0f)
                lineTo(290.0f, 510.0f)
                lineTo(290.0f, 600.0f)
                close()
                moveTo(160.0f, 640.0f)
                quadTo(160.0f, 640.0f, 160.0f, 640.0f)
                quadTo(160.0f, 640.0f, 160.0f, 640.0f)
                lineTo(160.0f, 320.0f)
                quadTo(160.0f, 320.0f, 160.0f, 320.0f)
                quadTo(160.0f, 320.0f, 160.0f, 320.0f)
                lineTo(160.0f, 320.0f)
                quadTo(160.0f, 320.0f, 160.0f, 320.0f)
                quadTo(160.0f, 320.0f, 160.0f, 320.0f)
                lineTo(160.0f, 640.0f)
                quadTo(160.0f, 640.0f, 160.0f, 640.0f)
                quadTo(160.0f, 640.0f, 160.0f, 640.0f)
                close()
            }
        }
            .build()
        return _buttonsAlt!!
    }

private var _buttonsAlt: ImageVector? = null
