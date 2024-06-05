package com.sorrowblue.comicviewer.framework.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.window.core.layout.WindowWidthSizeClass
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberWindowAdaptiveInfo
import kotlinx.coroutines.flow.flowOf

@Composable
fun rememberMobile(): Boolean {
    val windowAdaptiveInfo by rememberWindowAdaptiveInfo()
    return remember(windowAdaptiveInfo.windowSizeClass) {
        windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
    }
}

fun <T : Any> PagingData.Companion.flowEmptyData() = flowOf(
    empty<T>(
        LoadStates(
            refresh = LoadState.NotLoading(true),
            append = LoadState.NotLoading(true),
            prepend = LoadState.NotLoading(true),
        )
    )
)

fun <T : Any> PagingData.Companion.flowData(data: List<T>) = flowOf(from(data = data))
