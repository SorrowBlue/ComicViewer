package com.sorrowblue.comicviewer.feature.authentication

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal expect fun PinDrawable(
    index: Int,
    modifier: Modifier = Modifier,
    animate: Boolean = true,
    enabled: Boolean = true,
)
