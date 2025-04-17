package com.sorrowblue.comicviewer.framework.ui.preview.layout

import androidx.compose.runtime.Composable

@Composable
expect fun PreviewDevice(
    config: PreviewConfig = PreviewConfig(),
    content: @Composable () -> Unit,
)
