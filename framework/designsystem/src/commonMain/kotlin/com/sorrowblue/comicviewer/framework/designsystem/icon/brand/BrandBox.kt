@file:Suppress("detekt.all")

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

@Suppress("UnusedReceiverParameter")
val ComicIcons.BrandBox: ImageVector
    get() {
        if (brandBox != null) {
            return brandBox!!
        }
        brandBox = ImageVector.Builder(
            name = "BrandBox",
            defaultWidth = 613.56.dp,
            defaultHeight = 324.93.dp,
            viewportWidth = 613.56F,
            viewportHeight = 324.93F,
        ).path(
            fill = SolidColor(Color(0xFF3C5EB4)),
            fillAlpha = 1.0F,
            strokeAlpha = 1.0F,
            strokeLineWidth = 0.0F,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0F,
            pathFillType = PathFillType.NonZero,
        ) {
            moveToRelative(316.71F, 279.1F)
            curveToRelative(-37.93F, -0.0F, -68.69F, -30.73F, -68.69F, -68.66F)
            curveToRelative(0.0F, -37.9F, 30.76F, -68.62F, 68.69F, -68.62F)
            curveToRelative(37.92F, -0.0F, 68.66F, 30.72F, 68.66F, 68.62F)
            curveToRelative(0.0F, 37.93F, -30.74F, 68.66F, -68.66F, 68.66F)
            moveToRelative(-202.26F, -0.0F)
            curveToRelative(-37.92F, -0.0F, -68.7F, -30.72F, -68.7F, -68.65F)
            curveToRelative(0.0F, -37.92F, 30.78F, -68.64F, 68.7F, -68.64F)
            curveToRelative(37.92F, -0.0F, 68.64F, 30.72F, 68.64F, 68.62F)
            curveToRelative(0.0F, 37.93F, -30.71F, 68.66F, -68.64F, 68.66F)
            moveTo(316.71F, 95.99F)
            curveToRelative(-43.8F, -0.0F, -81.92F, 24.64F, -101.12F, 60.81F)
            curveToRelative(-19.2F, -36.18F, -57.3F, -60.81F, -101.14F, -60.81F)
            curveToRelative(-25.74F, -0.0F, -49.52F, 8.51F, -68.7F, 22.89F)
            lineToRelative(0.0F, -96.44F)
            curveToRelative(-0.23F, -12.46F, -10.39F, -22.44F, -22.9F, -22.44F)
            curveTo(10.35F, 0.01F, 0.29F, 9.98F, 0.0F, 22.44F)
            lineTo(0.0F, 212.35F)
            lineTo(0.03F, 212.35F)
            curveTo(1.03F, 274.7F, 51.85F, 324.93F, 114.46F, 324.93F)
            curveTo(158.29F, 324.93F, 196.39F, 300.27F, 215.59F, 264.13F)
            curveTo(234.79F, 300.27F, 272.91F, 324.93F, 316.71F, 324.93F)
            curveToRelative(63.2F, -0.0F, 114.47F, -51.25F, 114.47F, -114.49F)
            curveToRelative(0.0F, -63.22F, -51.27F, -114.45F, -114.47F, -114.45F)
            close()
        }.path(
            fill = SolidColor(Color(0xFF3C5EB4)),
            fillAlpha = 1.0F,
            strokeAlpha = 1.0F,
            strokeLineWidth = 0.0F,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0F,
            pathFillType = PathFillType.NonZero,
        ) {
            moveToRelative(608.8F, 286.61F)
            lineToRelative(-62.22F, -76.32F)
            lineToRelative(62.3F, -76.47F)
            curveToRelative(7.87F, -10.09F, 5.62F, -24.17F, -5.26F, -31.66F)
            curveToRelative(-10.89F, -7.55F, -26.16F, -5.61F, -34.58F, 4.18F)
            lineToRelative(0.0F, -0.02F)
            lineToRelative(-53.59F, 65.69F)
            lineToRelative(-53.55F, -65.69F)
            lineToRelative(0.0F, 0.02F)
            curveToRelative(-8.34F, -9.79F, -23.71F, -11.72F, -34.56F, -4.18F)
            curveToRelative(-10.85F, 7.49F, -13.12F, 21.57F, -5.21F, 31.66F)
            lineToRelative(-0.02F, -0.0F)
            lineTo(484.29F, 210.29F)
            lineTo(422.11F, 286.61F)
            lineToRelative(0.02F, -0.0F)
            curveTo(414.21F, 296.73F, 416.48F, 310.77F, 427.33F, 318.28F)
            curveTo(438.19F, 325.8F, 453.56F, 323.89F, 461.89F, 314.08F)
            lineTo(515.44F, 248.49F)
            lineTo(568.96F, 314.08F)
            curveToRelative(8.43F, 9.8F, 23.7F, 11.72F, 34.59F, 4.2F)
            curveToRelative(10.87F, -7.51F, 13.14F, -21.56F, 5.25F, -31.67F)
            close()
        }.build()
        return brandBox!!
    }
private var brandBox: ImageVector? = null

@Preview
@Composable
private fun IconBrandBoxPreview() {
    Image(imageVector = ComicIcons.BrandBox, contentDescription = null)
}
