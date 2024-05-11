package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.calculatePosture
import androidx.compose.runtime.compositionLocalOf
import androidx.window.core.layout.WindowSizeClass

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
val LocalWindowAdaptiveInfo =
    compositionLocalOf {
        WindowAdaptiveInfo(
            WindowSizeClass.compute(0f, 0f),
            calculatePosture(emptyList())
        )
    }
