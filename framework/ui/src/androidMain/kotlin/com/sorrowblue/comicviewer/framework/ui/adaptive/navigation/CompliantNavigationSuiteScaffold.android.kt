package com.sorrowblue.comicviewer.framework.ui.adaptive.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalContainerColor
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.fake.nextLoremIpsum
import com.sorrowblue.comicviewer.framework.ui.preview.layout.PreviewConfig
import com.sorrowblue.comicviewer.framework.ui.preview.layout.PreviewDevice
import com.sorrowblue.comicviewer.framework.ui.preview.layout.scratch

@PreviewMultiScreen
@Composable
private fun CompliantNavigationSuiteScaffoldPreview(
    @PreviewParameter(PreviewConfigProvider::class) config: PreviewConfig,
) {
    PreviewDevice(config = config) {
        CompliantNavigationSuiteScaffold(
            navigationSuiteItems = {
                repeat(5) {
                    item(
                        selected = it == 0,
                        onClick = {},
                        icon = { Icon(ComicIcons.Edit, null) },
                        label = { Text(nextLoremIpsum().take(8)) }
                    )
                }
            },
        ) {
            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing,
                containerColor = LocalContainerColor.current,
            ) { contentPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scratch(Color.Red)
                        .padding(contentPadding)
                        .scratch(Color.Blue)
                )
            }
        }
    }
}

private class PreviewConfigProvider : PreviewParameterProvider<PreviewConfig> {
    override val values
        get() = sequenceOf(
            PreviewConfig(isInvertedOrientation = false, cutout = true, systemBar = true),
            PreviewConfig(isInvertedOrientation = true, cutout = true, systemBar = true),
            PreviewConfig(isInvertedOrientation = false, cutout = true, systemBar = false),
            PreviewConfig(isInvertedOrientation = true, cutout = true, systemBar = false),
            PreviewConfig(isInvertedOrientation = false, cutout = false, systemBar = true),
            PreviewConfig(isInvertedOrientation = false, cutout = false, systemBar = false),
        )
}
