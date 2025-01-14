package com.sorrowblue.comicviewer.framework.designsystem.icon

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.theme.source
import org.jetbrains.compose.ui.tooling.preview.Preview

@Suppress("UnusedReceiverParameter")
val ComicIcons.Launcher: ImageVector
    get() {
        if (launcher != null) {
            return launcher!!
        }
        launcher = ImageVector.Builder(
            name = "Launcher",
            defaultWidth = 128.0.dp,
            defaultHeight = 128.0.dp,
            viewportWidth = 128.0f,
            viewportHeight = 128.0f
        ).apply {
            path(fill = SolidColor(source)) {
                moveTo(-0.0f, 97.0f)
                lineTo(59.345f, 97.0f)
                lineTo(46.922f, 128.0f)
                lineTo(0.0f, 128.0f)
                close()
                moveTo(128.0f, 97.0f)
                lineTo(128.0f, 128.0f)
                lineTo(56.003f, 128.0f)
                lineTo(68.426f, 97.0f)
                close()
            }
            path(fill = SolidColor(source)) {
                moveTo(0.0f, 48.5f)
                lineTo(92.086f, 48.5f)
                lineTo(92.086f, 79.5f)
                lineTo(0.0f, 79.5f)
                close()
                moveTo(128.0f, 48.5f)
                lineTo(128.0f, 79.5f)
                lineTo(100.642f, 79.5f)
                lineTo(100.642f, 48.5f)
                close()
            }
            path(fill = SolidColor(source)) {
                moveTo(-0.0f, 0.0f)
                lineTo(43.827f, 0.0f)
                lineTo(19.927f, 31.0f)
                lineTo(-0.0f, 31.0f)
                close()
                moveTo(128.0f, 0.0f)
                lineTo(128.0f, 31.0f)
                lineTo(30.344f, 31.0f)
                lineTo(54.245f, 0.0f)
                close()
            }
        }
            .build()
        return launcher!!
    }
private var launcher: ImageVector? = null

@Preview
@Composable
private fun IconLauncherPreview() {
    Image(imageVector = ComicIcons.Launcher, contentDescription = null)
}
