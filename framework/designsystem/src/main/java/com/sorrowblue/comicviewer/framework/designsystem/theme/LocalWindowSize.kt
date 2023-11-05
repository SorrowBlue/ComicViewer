package com.sorrowblue.comicviewer.framework.designsystem.theme

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.DpSize

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
val LocalWindowSize =
    staticCompositionLocalOf { WindowSizeClass.calculateFromSize(DpSize.Unspecified) }
