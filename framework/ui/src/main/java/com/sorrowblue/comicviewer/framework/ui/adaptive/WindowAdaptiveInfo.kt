package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.calculatePosture
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun rememberWindowAdaptiveInfo(): State<WindowAdaptiveInfo> {
    val windowAdaptiveInfo = if (LocalInspectionMode.current) {
        val configuration = LocalConfiguration.current
        val windowSizeClass = with(LocalDensity.current) {
            WindowSizeClass.compute(
                configuration.screenWidthDp.dp.toPx(),
                configuration.screenHeightDp.dp.toPx()
            )
        }
        WindowAdaptiveInfo(windowSizeClass, calculatePosture(emptyList()))
    } else {
        currentWindowAdaptiveInfo()
    }
    return remember(windowAdaptiveInfo) {
        mutableStateOf(windowAdaptiveInfo)
    }
}
