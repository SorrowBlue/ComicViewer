package com.sorrowblue.comicviewer.feature.settings.common

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.window.core.layout.WindowSizeClass
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

interface SettingsDetailNavigator {
    fun navigateBack()
}

interface SettingsExtraNavigator {
    fun navigateUp()
}

@Composable
fun SettingsDetailPane(
    title: @Composable () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHost: @Composable () -> Unit = {},
    actions: @Composable (RowScope.() -> Unit) = {},
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
    scrollBehavior: TopAppBarScrollBehavior =
        if (windowAdaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        } else {
            TopAppBarDefaults.pinnedScrollBehavior()
        },
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable (ColumnScope.() -> Unit),
) {
    Scaffold(
        topBar = {
            if (windowAdaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(
                    WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
                )
            ) {
                LargeTopAppBar(
                    title = title,
                    actions = actions,
                    windowInsets = WindowInsets.safeDrawing
                        .only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
                    scrollBehavior = scrollBehavior
                )
            } else {
                TopAppBar(
                    title = title,
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = ComicIcons.ArrowBack,
                                contentDescription = "上へ移動"
                            )
                        }
                    },
                    actions = actions,
                    windowInsets = WindowInsets.safeDrawing
                        .only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
                    scrollBehavior = scrollBehavior
                )
            }
        },
        snackbarHost = snackbarHost,
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                // TODO .drawVerticalScrollbar(scrollState)
                .verticalScroll(scrollState)
                .padding(innerPadding),
            content = content
        )
    }
}
