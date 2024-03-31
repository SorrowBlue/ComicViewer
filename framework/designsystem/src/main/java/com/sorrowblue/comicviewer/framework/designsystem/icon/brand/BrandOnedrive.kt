package com.sorrowblue.comicviewer.framework.designsystem.icon.brand

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
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

val ComicIcons.BrandOnedrive: ImageVector
    get() {
        if (_brandOnedrive != null) {
            return _brandOnedrive!!
        }
        _brandOnedrive = ImageVector.Builder(
            name = "BrandOnedrive",
            defaultWidth = 32.0.dp,
            defaultHeight = 20.5.dp,
            viewportWidth = 32.0F,
            viewportHeight = 20.5F,
        ).path(
            fill = SolidColor(Color(0xFF0364B8)),
            fillAlpha = 1.0F,
            strokeAlpha = 1.0F,
            strokeLineWidth = 0.0F,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0F,
            pathFillType = PathFillType.NonZero,
        ) {
            moveTo(12.202F, 5.693F)
            lineToRelative(0.0F, -0.001F)
            lineToRelative(6.718F, 4.024F)
            lineToRelative(4.003F, -1.685F)
            lineToRelative(0.0F, 0.001F)
            arcTo(
                6.477F, 6.477F, 0.0F,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 25.5F,
                y1 = 7.5F
            )
            curveToRelative(0.148F, 0.0F, 0.294F, 0.007F, 0.439F, 0.016F)
            arcToRelative(10.001F, 10.001F, 0.0F, false, false, -18.041F, -3.014F)
            curveTo(7.932F, 4.502F, 7.966F, 4.5F, 8.0F, 4.5F)
            arcTo(7.961F, 7.961F, 0.0F, false, true, 12.202F, 5.693F)
            close()
        }.path(
            fill = SolidColor(Color(0xFF0078D4)),
            fillAlpha = 1.0F,
            strokeAlpha = 1.0F,
            strokeLineWidth = 0.0F,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0F,
            pathFillType = PathFillType.NonZero,
        ) {
            moveTo(12.203F, 5.692F)
            lineToRelative(-0.0F, 0.001F)
            arcTo(7.961F, 7.961F, 0.0F, false, false, 8.0F, 4.5F)
            curveToRelative(-0.034F, 0.0F, -0.068F, 0.002F, -0.102F, 0.003F)
            arcTo(7.997F, 7.997F, 0.0F, false, false, 1.437F, 17.073F)
            lineToRelative(5.924F, -2.493F)
            lineToRelative(2.633F, -1.108F)
            lineToRelative(5.864F, -2.467F)
            lineToRelative(3.062F, -1.289F)
            close()
        }.path(
            fill = SolidColor(Color(0xFF1490DF)),
            fillAlpha = 1.0F,
            strokeAlpha = 1.0F,
            strokeLineWidth = 0.0F,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0F,
            pathFillType = PathFillType.NonZero,
        ) {
            moveTo(25.939F, 7.516F)
            curveTo(25.794F, 7.507F, 25.648F, 7.5F, 25.5F, 7.5F)
            arcToRelative(6.477F, 6.477F, 0.0F, false, false, -2.576F, 0.532F)
            lineToRelative(-0.0F, -0.001F)
            lineToRelative(-4.003F, 1.685F)
            lineToRelative(1.161F, 0.695F)
            lineTo(23.886F, 12.69F)
            lineToRelative(1.66F, 0.994F)
            lineToRelative(5.676F, 3.4F)
            arcToRelative(6.5F, 6.5F, 0.0F, false, false, -5.284F, -9.568F)
            close()
        }.path(
            fill = SolidColor(Color(0xFF28A8EA)),
            fillAlpha = 1.0F,
            strokeAlpha = 1.0F,
            strokeLineWidth = 0.0F,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0F,
            pathFillType = PathFillType.NonZero,
        ) {
            moveTo(25.546F, 13.684F)
            lineTo(23.886F, 12.69F)
            lineToRelative(-3.805F, -2.279F)
            lineToRelative(-1.161F, -0.695F)
            lineTo(15.858F, 11.004F)
            lineTo(9.995F, 13.472F)
            lineTo(7.361F, 14.58F)
            lineToRelative(-5.924F, 2.493F)
            arcTo(7.989F, 7.989F, 0.0F, false, false, 8.0F, 20.5F)
            lineTo(25.5F, 20.5F)
            arcToRelative(6.498F, 6.498F, 0.0F, false, false, 5.723F, -3.416F)
            close()
        }.build()
        return _brandOnedrive!!
    }
private var _brandOnedrive: ImageVector? = null

@Preview
@Composable
private fun IconBrandOnedrivePreview() {
    Image(imageVector = ComicIcons.BrandOnedrive, contentDescription = null)
}
