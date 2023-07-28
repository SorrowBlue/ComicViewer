package com.sorrowblue.comicviewer.framework.compose

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Stable
class Dimensions(
    val margin: Dp,
    val spacer: Dp
)

val compactDimensions = Dimensions(
    16.dp,
    16.dp,
)

val mediumDimensions = Dimensions(
    24.dp,
    24.dp,
)

val expandedDimensions = Dimensions(
    24.dp,
    24.dp,
)
