package com.sorrowblue.comicviewer.framework.designsystem.icon.symbols

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
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
import org.jetbrains.compose.ui.tooling.preview.Preview

@Suppress("UnusedReceiverParameter")
val ComicIcons.DocumentUnknown: ImageVector
    get() {
        if (_documentUnknown != null) {
            return _documentUnknown!!
        }
        _documentUnknown = Builder(
            name =
            "_documentUnknown",
            defaultWidth = 24.0.dp,
            defaultHeight =
            24.0.dp,
            viewportWidth = 960.0f, viewportHeight = 960.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(200.0f, 160.0f)
                verticalLineToRelative(640.0f)
                verticalLineToRelative(-640.0f)
                verticalLineToRelative(200.0f)
                verticalLineToRelative(-200.0f)
                close()
                moveTo(280.0f, 560.0f)
                horizontalLineToRelative(147.0f)
                quadToRelative(11.0f, -23.0f, 25.5f, -43.0f)
                reflectiveQuadToRelative(32.5f, -37.0f)
                lineTo(280.0f, 480.0f)
                verticalLineToRelative(80.0f)
                close()
                moveTo(280.0f, 720.0f)
                horizontalLineToRelative(123.0f)
                quadToRelative(-3.0f, -20.0f, -3.0f, -40.0f)
                reflectiveQuadToRelative(3.0f, -40.0f)
                lineTo(280.0f, 640.0f)
                verticalLineToRelative(80.0f)
                close()
                moveTo(200.0f, 880.0f)
                quadToRelative(-33.0f, 0.0f, -56.5f, -23.5f)
                reflectiveQuadTo(120.0f, 800.0f)
                verticalLineToRelative(-640.0f)
                quadToRelative(0.0f, -33.0f, 23.5f, -56.5f)
                reflectiveQuadTo(200.0f, 80.0f)
                horizontalLineToRelative(320.0f)
                lineToRelative(240.0f, 240.0f)
                verticalLineToRelative(92.0f)
                quadToRelative(-19.0f, -6.0f, -39.0f, -9.0f)
                reflectiveQuadToRelative(-41.0f, -3.0f)
                verticalLineToRelative(-40.0f)
                lineTo(480.0f, 360.0f)
                verticalLineToRelative(-200.0f)
                lineTo(200.0f, 160.0f)
                verticalLineToRelative(640.0f)
                horizontalLineToRelative(227.0f)
                quadToRelative(11.0f, 23.0f, 25.5f, 43.0f)
                reflectiveQuadTo(485.0f, 880.0f)
                lineTo(200.0f, 880.0f)
                close()
                moveTo(680.0f, 480.0f)
                quadToRelative(83.0f, 0.0f, 141.5f, 58.5f)
                reflectiveQuadTo(880.0f, 680.0f)
                quadToRelative(0.0f, 83.0f, -58.5f, 141.5f)
                reflectiveQuadTo(680.0f, 880.0f)
                quadToRelative(-83.0f, 0.0f, -141.5f, -58.5f)
                reflectiveQuadTo(480.0f, 680.0f)
                quadToRelative(0.0f, -83.0f, 58.5f, -141.5f)
                reflectiveQuadTo(680.0f, 480.0f)
                close()
                moveTo(680.0f, 800.0f)
                quadToRelative(11.0f, 0.0f, 18.5f, -7.5f)
                reflectiveQuadTo(706.0f, 774.0f)
                quadToRelative(0.0f, -11.0f, -7.5f, -18.5f)
                reflectiveQuadTo(680.0f, 748.0f)
                quadToRelative(-11.0f, 0.0f, -18.5f, 7.5f)
                reflectiveQuadTo(654.0f, 774.0f)
                quadToRelative(0.0f, 11.0f, 7.5f, 18.5f)
                reflectiveQuadTo(680.0f, 800.0f)
                close()
                moveTo(662.0f, 724.0f)
                horizontalLineToRelative(36.0f)
                verticalLineToRelative(-10.0f)
                quadToRelative(0.0f, -11.0f, 6.0f, -19.5f)
                reflectiveQuadToRelative(14.0f, -16.5f)
                quadToRelative(14.0f, -12.0f, 22.0f, -23.0f)
                reflectiveQuadToRelative(8.0f, -31.0f)
                quadToRelative(0.0f, -29.0f, -19.0f, -46.5f)
                reflectiveQuadTo(680.0f, 560.0f)
                quadToRelative(-23.0f, 0.0f, -41.5f, 13.5f)
                reflectiveQuadTo(612.0f, 610.0f)
                lineToRelative(32.0f, 14.0f)
                quadToRelative(3.0f, -12.0f, 12.5f, -21.0f)
                reflectiveQuadToRelative(23.5f, -9.0f)
                quadToRelative(15.0f, 0.0f, 23.5f, 7.5f)
                reflectiveQuadTo(712.0f, 624.0f)
                quadToRelative(0.0f, 11.0f, -6.0f, 18.5f)
                reflectiveQuadTo(692.0f, 658.0f)
                quadToRelative(-6.0f, 6.0f, -12.5f, 12.0f)
                reflectiveQuadTo(668.0f, 684.0f)
                quadToRelative(-3.0f, 6.0f, -4.5f, 12.0f)
                reflectiveQuadToRelative(-1.5f, 14.0f)
                verticalLineToRelative(14.0f)
                close()
            }
        }
            .build()
        return _documentUnknown!!
    }

@Suppress("ObjectPropertyName")
private var _documentUnknown: ImageVector? = null

@Preview
@Composable
private fun DocumentUnknownPreview() {
    Icon(imageVector = ComicIcons.DocumentUnknown, contentDescription = null)
}
