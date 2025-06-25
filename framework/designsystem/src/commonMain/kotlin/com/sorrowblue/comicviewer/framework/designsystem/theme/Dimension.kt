package com.sorrowblue.comicviewer.framework.designsystem.theme

import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Stable
data class Dimension(
    val margin: Dp,
    val spacer: Dp,
    val targetSpacing: Dp = 8.dp,
    val minPadding: Dp = 4.dp,
    val padding: Dp = 8.dp,
)

val compactDimension = Dimension(
    16.dp,
    16.dp,
)

val mediumDimension = Dimension(
    24.dp,
    24.dp,
)

val expandedDimension = Dimension(
    24.dp,
    24.dp,
)

val LocalDimension = staticCompositionLocalOf {
    compactDimension
}
