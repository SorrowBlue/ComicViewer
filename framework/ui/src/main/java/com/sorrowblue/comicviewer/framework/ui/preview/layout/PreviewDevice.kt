package com.sorrowblue.comicviewer.framework.ui.preview.layout

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import de.drick.compose.edgetoedgepreviewlib.CameraCutoutMode
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import de.drick.compose.edgetoedgepreviewlib.InsetMode
import de.drick.compose.edgetoedgepreviewlib.InsetsConfig

@Composable
fun PreviewDevice(
    config: PreviewConfig = PreviewConfig(),
    content: @Composable () -> Unit,
) {
    PreviewTheme {
        EdgeToEdgeTemplate(
            cfg = InsetsConfig.GestureNav.copy(
                isInvertedOrientation = config.isInvertedOrientation,
                cameraCutoutMode = if (config.cutout) CameraCutoutMode.Middle else CameraCutoutMode.None,
                statusBarMode = if (config.systemBar) InsetMode.Visible else InsetMode.Hidden,
                navigationBarMode = if (config.systemBar) InsetMode.Visible else InsetMode.Hidden
            ),
            isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE,
        ) {
            content()
        }
    }
}
