package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.calculatePosture
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.DpSize

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
val LocalWindowAdaptiveInfo =
    compositionLocalOf {
        WindowAdaptiveInfo(
            WindowSizeClass.calculateFromSize(DpSize.Unspecified),
            calculatePosture(emptyList())
        )
    }
