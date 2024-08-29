package com.sorrowblue.comicviewer.framework.ui.adaptive

import android.content.res.Configuration
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.calculatePosture
import androidx.compose.material3.adaptive.collectFoldingFeaturesAsState
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.toSize
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun rememberWindowAdaptiveInfo(): State<WindowAdaptiveInfo> {
    val windowAdaptiveInfo = if (LocalInspectionMode.current) {
        val configuration = LocalConfiguration.current
        val windowSizeClass =
            if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
                WindowSizeClass.compute(
                    configuration.screenWidthDp.toFloat(),
                    configuration.screenHeightDp.toFloat()
                )
            } else {
                WindowSizeClass.compute(
                    configuration.screenHeightDp.toFloat(),
                    configuration.screenWidthDp.toFloat()
                )
            }
        WindowAdaptiveInfo(windowSizeClass, calculatePosture(emptyList()))
    } else {
        val windowSize = with(LocalDensity.current) { currentWindowSize().toSize().toDpSize() }
        WindowAdaptiveInfo(
            if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
                WindowSizeClass.compute(windowSize.width.value, windowSize.height.value)
            } else {
                WindowSizeClass.compute(windowSize.height.value, windowSize.width.value)
            },
            calculatePosture(collectFoldingFeaturesAsState().value)
        )
    }
    return remember(windowAdaptiveInfo) {
        mutableStateOf(windowAdaptiveInfo)
    }
}

val WindowAdaptiveInfo.isCompact
    get() = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
