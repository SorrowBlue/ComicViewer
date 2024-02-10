package com.sorrowblue.comicviewer.framework.designsystem.icon

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val ComicIcons.Launcher: ImageVector
    get() {
        if (_launcher != null) {
            return _launcher!!
        }
        _launcher = ImageVector.Builder(
            name = "Launcher",
            defaultWidth = 108.0.dp,
            defaultHeight = 108.0.dp,
            viewportWidth = 108.0F,
            viewportHeight = 108.0F,
        ).path(
            fill = SolidColor(Color(0xFF77DB98)),
            fillAlpha = 1.0F,
            strokeAlpha = 1.0F,
            strokeLineWidth = 0.0F,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0F,
            pathFillType = PathFillType.NonZero,
        ) {
            moveTo(23.92F, 70.0F)
            lineTo(51.74F, 70.0F)
            lineTo(45.91F, 86.0F)
            lineTo(23.92F, 86.0F)

            moveTo(83.92F, 70.0F)
            lineTo(83.92F, 86.0F)
            lineTo(50.17F, 86.0F)
            lineTo(55.99F, 70.0F)
            close()
        }.path(
            fill = SolidColor(Color(0xFF77DB98)),
            fillAlpha = 1.0F,
            strokeAlpha = 1.0F,
            strokeLineWidth = 0.0F,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0F,
            pathFillType = PathFillType.NonZero,
        ) {
            moveTo(24.16F, 46.0F)
            lineTo(67.21F, 46.0F)
            lineTo(67.21F, 62.0F)
            lineTo(24.16F, 62.0F)

            moveTo(84.0F, 46.0F)
            lineTo(84.0F, 62.0F)
            lineTo(71.21F, 62.0F)
            lineTo(71.21F, 46.0F)
            close()
        }.path(
            fill = SolidColor(Color(0xFF77DB98)),
            fillAlpha = 1.0F,
            strokeAlpha = 1.0F,
            strokeLineWidth = 0.0F,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0F,
            pathFillType = PathFillType.NonZero,
        ) {
            moveTo(23.92F, 22.0F)
            lineTo(44.46F, 22.0F)
            lineTo(33.26F, 38.0F)
            lineTo(23.92F, 38.0F)

            moveTo(83.92F, 22.0F)
            lineTo(83.92F, 38.0F)
            lineTo(38.14F, 38.0F)
            lineTo(49.35F, 22.0F)
            close()
        }.build()
        return _launcher!!
    }
private var _launcher: ImageVector? = null

@Preview
@Composable
private fun IconLauncherPreview() {
    Image(imageVector = ComicIcons.Launcher, contentDescription = null)
}
