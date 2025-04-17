package com.sorrowblue.comicviewer.framework.designsystem.icon.undraw

import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import org.jetbrains.compose.ui.tooling.preview.Preview

@Suppress("UnusedReceiverParameter")
val ComicIcons.UndrawNoData: ImageVector
    @Composable get() {
        if (undrawNoData != null) {
            return undrawNoData!!
        }
        undrawNoData = ImageVector.Builder(
            name = "UndrawNoData",
            defaultWidth = 647.64.dp,
            defaultHeight = 632.17.dp,
            viewportWidth = 647.64F,
            viewportHeight = 632.17F,
        ).path(
            fill = SolidColor(Color(0xFFF2F2F2)),
            fillAlpha = 1.0F,
            strokeAlpha = 1.0F,
            strokeLineWidth = 0.0F,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0F,
            pathFillType = PathFillType.NonZero,
        ) {
            moveTo(411.15F, 142.17F)
            lineTo(236.64F, 142.17F)
            arcToRelative(15.02F, 15.02F, 0.0F, false, false, -15.0F, 15.0F)
            verticalLineToRelative(387.85F)
            lineToRelative(-2.0F, 0.61F)
            lineToRelative(-42.81F, 13.11F)
            arcToRelative(8.01F, 8.01F, 0.0F, false, true, -9.99F, -5.31F)
            lineTo(39.5F, 137.48F)
            arcToRelative(8.0F, 8.0F, 0.0F, false, true, 5.31F, -9.99F)
            lineToRelative(65.97F, -20.2F)
            lineToRelative(191.25F, -58.54F)
            lineToRelative(65.97F, -20.2F)
            arcToRelative(7.99F, 7.99F, 0.0F, false, true, 9.99F, 5.3F)
            lineToRelative(32.55F, 106.32F)
            close()
        }.path(
            fill = SolidColor(Color(0xFF3F3D56)),
            fillAlpha = 1.0F,
            strokeAlpha = 1.0F,
            strokeLineWidth = 0.0F,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0F,
            pathFillType = PathFillType.NonZero,
        ) {
            moveTo(449.23F, 140.17F)
            lineToRelative(-39.23F, -128.14F)
            arcToRelative(16.99F, 16.99F, 0.0F, false, false, -21.23F, -11.28F)
            lineToRelative(-92.75F, 28.39F)
            lineTo(104.78F, 87.69F)
            lineToRelative(-92.75F, 28.4F)
            arcToRelative(17.02F, 17.02F, 0.0F, false, false, -11.28F, 21.23F)
            lineToRelative(134.08F, 437.93F)
            arcToRelative(17.03F, 17.03F, 0.0F, false, false, 16.26F, 12.03F)
            arcToRelative(16.79F, 16.79F, 0.0F, false, false, 4.97F, -0.75F)
            lineToRelative(63.58F, -19.46F)
            lineToRelative(2.0F, -0.62F)
            verticalLineToRelative(-2.09F)
            lineToRelative(-2.0F, 0.61F)
            lineToRelative(-64.17F, 19.65F)
            arcToRelative(15.01F, 15.01F, 0.0F, false, true, -18.73F, -9.95F)
            lineToRelative(-134.07F, -437.94F)
            arcToRelative(14.98F, 14.98F, 0.0F, false, true, 9.95F, -18.73F)
            lineToRelative(92.75F, -28.4F)
            lineToRelative(191.24F, -58.54F)
            lineToRelative(92.75F, -28.4F)
            arcToRelative(15.16F, 15.16F, 0.0F, false, true, 4.41F, -0.66F)
            arcToRelative(15.01F, 15.01F, 0.0F, false, true, 14.32F, 10.61F)
            lineToRelative(39.05F, 127.56F)
            lineToRelative(0.62F, 2.0F)
            horizontalLineToRelative(2.08F)
            close()
        }.path(
            fill = SolidColor(MaterialTheme.colorScheme.primary),
            fillAlpha = 1.0F,
            strokeAlpha = 1.0F,
            strokeLineWidth = 0.0F,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0F,
            pathFillType = PathFillType.NonZero,
        ) {
            moveTo(122.68F, 127.82F)
            arcToRelative(9.02F, 9.02F, 0.0F, false, true, -8.61F, -6.37F)
            lineToRelative(-12.88F, -42.07F)
            arcToRelative(9.0F, 9.0F, 0.0F, false, true, 5.97F, -11.24F)
            lineToRelative(175.94F, -53.86F)
            arcToRelative(9.01F, 9.01F, 0.0F, false, true, 11.24F, 5.97F)
            lineToRelative(12.88F, 42.07F)
            arcToRelative(9.01F, 9.01F, 0.0F, false, true, -5.97F, 11.24F)
            lineTo(125.31F, 127.43F)
            arcTo(8.98F, 8.98F, 0.0F, false, true, 122.68F, 127.82F)
            close()
        }.path(
            fill = SolidColor(MaterialTheme.colorScheme.primary),
            fillAlpha = 1.0F,
            strokeAlpha = 1.0F,
            strokeLineWidth = 0.0F,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0F,
            pathFillType = PathFillType.NonZero,
        ) {
            moveTo(190.15F, 24.95F)
            moveToRelative(-20.0F, 0.0F)
            arcToRelative(20.0F, 20.0F, 0.0F, true, true, 40.0F, 0.0F)
            arcToRelative(20.0F, 20.0F, 0.0F, true, true, -40.0F, 0.0F)
            close()
        }.path(
            fill = SolidColor(Color(0xFFFFFFFF)),
            fillAlpha = 1.0F,
            strokeAlpha = 1.0F,
            strokeLineWidth = 0.0F,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0F,
            pathFillType = PathFillType.NonZero,
        ) {
            moveTo(190.15F, 24.95F)
            moveToRelative(-12.66F, 0.0F)
            arcToRelative(12.66F, 12.66F, 0.0F, true, true, 25.33F, 0.0F)
            arcToRelative(12.66F, 12.66F, 0.0F, true, true, -25.33F, 0.0F)
            close()
        }.path(
            fill = SolidColor(Color(0xFFE6E6E6)),
            fillAlpha = 1.0F,
            strokeAlpha = 1.0F,
            strokeLineWidth = 0.0F,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0F,
            pathFillType = PathFillType.NonZero,
        ) {
            moveTo(602.64F, 582.17F)
            horizontalLineToRelative(-338.0F)
            arcToRelative(8.51F, 8.51F, 0.0F, false, true, -8.5F, -8.5F)
            verticalLineToRelative(-405.0F)
            arcToRelative(8.51F, 8.51F, 0.0F, false, true, 8.5F, -8.5F)
            horizontalLineToRelative(338.0F)
            arcToRelative(8.51F, 8.51F, 0.0F, false, true, 8.5F, 8.5F)
            verticalLineToRelative(405.0F)
            arcTo(8.51F, 8.51F, 0.0F, false, true, 602.64F, 582.17F)
            close()
        }.path(
            fill = SolidColor(Color(0xFF3F3D56)),
            fillAlpha = 1.0F,
            strokeAlpha = 1.0F,
            strokeLineWidth = 0.0F,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0F,
            pathFillType = PathFillType.NonZero,
        ) {
            moveTo(447.14F, 140.17F)
            horizontalLineToRelative(-210.5F)
            arcToRelative(17.02F, 17.02F, 0.0F, false, false, -17.0F, 17.0F)
            verticalLineToRelative(407.8F)
            lineToRelative(2.0F, -0.61F)
            verticalLineToRelative(-407.19F)
            arcToRelative(15.02F, 15.02F, 0.0F, false, true, 15.0F, -15.0F)
            lineTo(447.76F, 142.17F)

            moveTo(630.64F, 140.17F)
            horizontalLineToRelative(-394.0F)
            arcToRelative(17.02F, 17.02F, 0.0F, false, false, -17.0F, 17.0F)
            verticalLineToRelative(458.0F)
            arcToRelative(17.02F, 17.02F, 0.0F, false, false, 17.0F, 17.0F)
            horizontalLineToRelative(394.0F)
            arcToRelative(17.02F, 17.02F, 0.0F, false, false, 17.0F, -17.0F)
            verticalLineToRelative(-458.0F)
            arcTo(17.02F, 17.02F, 0.0F, false, false, 630.64F, 140.17F)

            moveTo(645.64F, 615.17F)
            arcToRelative(15.02F, 15.02F, 0.0F, false, true, -15.0F, 15.0F)
            horizontalLineToRelative(-394.0F)
            arcToRelative(15.02F, 15.02F, 0.0F, false, true, -15.0F, -15.0F)
            verticalLineToRelative(-458.0F)
            arcToRelative(15.02F, 15.02F, 0.0F, false, true, 15.0F, -15.0F)
            horizontalLineToRelative(394.0F)
            arcToRelative(15.02F, 15.02F, 0.0F, false, true, 15.0F, 15.0F)
            close()
        }.path(
            fill = SolidColor(MaterialTheme.colorScheme.primary),
            fillAlpha = 1.0F,
            strokeAlpha = 1.0F,
            strokeLineWidth = 0.0F,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0F,
            pathFillType = PathFillType.NonZero,
        ) {
            moveTo(525.64F, 184.17F)
            horizontalLineToRelative(-184.0F)
            arcToRelative(9.01F, 9.01F, 0.0F, false, true, -9.0F, -9.0F)
            verticalLineToRelative(-44.0F)
            arcToRelative(9.01F, 9.01F, 0.0F, false, true, 9.0F, -9.0F)
            horizontalLineToRelative(184.0F)
            arcToRelative(9.01F, 9.01F, 0.0F, false, true, 9.0F, 9.0F)
            verticalLineToRelative(44.0F)
            arcTo(9.01F, 9.01F, 0.0F, false, true, 525.64F, 184.17F)
            close()
        }.path(
            fill = SolidColor(MaterialTheme.colorScheme.primary),
            fillAlpha = 1.0F,
            strokeAlpha = 1.0F,
            strokeLineWidth = 0.0F,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0F,
            pathFillType = PathFillType.NonZero,
        ) {
            moveTo(433.64F, 105.17F)
            moveToRelative(-20.0F, 0.0F)
            arcToRelative(20.0F, 20.0F, 0.0F, true, true, 40.0F, 0.0F)
            arcToRelative(20.0F, 20.0F, 0.0F, true, true, -40.0F, 0.0F)
            close()
        }.path(
            fill = SolidColor(Color(0xFFFFFFFF)),
            fillAlpha = 1.0F,
            strokeAlpha = 1.0F,
            strokeLineWidth = 0.0F,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0F,
            pathFillType = PathFillType.NonZero,
        ) {
            moveTo(433.64F, 105.17F)
            moveToRelative(-12.18F, 0.0F)
            arcToRelative(12.18F, 12.18F, 0.0F, true, true, 24.36F, 0.0F)
            arcToRelative(12.18F, 12.18F, 0.0F, true, true, -24.36F, 0.0F)
            close()
        }.build()
        return undrawNoData!!
    }
private var undrawNoData: ImageVector? = null

@Preview
@Composable
private fun IconUndrawNoDataPreview() {
    Image(imageVector = ComicIcons.UndrawNoData, contentDescription = null)
}
