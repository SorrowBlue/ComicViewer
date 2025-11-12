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
val ComicIcons.BrandDropbox: ImageVector
    get() {
        if (brandDropbox != null) {
            return brandDropbox!!
        }
        brandDropbox = ImageVector.Builder(
            name = "BrandDropbox",
            defaultWidth = 43.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 43.0F,
            viewportHeight = 40.0F,
        ).path(
            fill = SolidColor(Color(0xFF007EE5)),
            fillAlpha = 1.0F,
            strokeAlpha = 1.0F,
            strokeLineWidth = 0.0F,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0F,
            pathFillType = PathFillType.NonZero,
        ) {
            moveToRelative(12.5F, 0.0F)
            lineToRelative(-12.5F, 8.1F)
            lineToRelative(8.7F, 7.0F)
            lineToRelative(12.5F, -7.8F)
            lineToRelative(-8.7F, -7.3F)

            moveTo(0.0F, 21.9F)
            lineToRelative(12.5F, 8.2F)
            lineToRelative(8.7F, -7.3F)
            lineToRelative(-12.5F, -7.7F)
            lineToRelative(-8.7F, 6.8F)

            moveTo(21.2F, 22.8F)
            lineToRelative(8.8F, 7.3F)
            lineToRelative(12.4F, -8.1F)
            lineToRelative(-8.6F, -6.9F)
            lineToRelative(-12.6F, 7.7F)

            moveTo(42.4F, 8.1F)
            lineToRelative(-12.4F, -8.1F)
            lineToRelative(-8.8F, 7.3F)
            lineToRelative(12.6F, 7.8F)
            lineToRelative(8.6F, -7.0F)

            moveTo(21.3F, 24.4F)
            lineToRelative(-8.8F, 7.3F)
            lineToRelative(-3.7F, -2.5F)
            verticalLineToRelative(2.8F)
            lineToRelative(12.5F, 7.5F)
            lineToRelative(12.5F, -7.5F)
            verticalLineToRelative(-2.8F)
            lineToRelative(-3.8F, 2.5F)
            lineToRelative(-8.7F, -7.3F)
            close()
        }.build()
        return brandDropbox!!
    }
private var brandDropbox: ImageVector? = null

@Preview
@Composable
private fun IconBrandDropboxPreview() {
    Image(imageVector = ComicIcons.BrandDropbox, contentDescription = null)
}
