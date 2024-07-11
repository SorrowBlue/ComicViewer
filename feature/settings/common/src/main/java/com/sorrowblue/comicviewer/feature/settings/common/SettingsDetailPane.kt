package com.sorrowblue.comicviewer.feature.settings.common

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.window.core.layout.WindowWidthSizeClass
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberWindowAdaptiveInfo
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar

interface SettingsDetailNavigator {
    fun navigateBack()
}

interface SettingsExtraNavigator {
    fun navigateUp()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDetailPane(
    title: @Composable () -> Unit,
    onBackClick: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    snackbarHost: @Composable () -> Unit = {},
    actions: @Composable (RowScope.() -> Unit) = {},
    widthSizeClass: WindowWidthSizeClass = rememberWindowAdaptiveInfo().value.windowSizeClass.windowWidthSizeClass,
    scrollBehavior: TopAppBarScrollBehavior =
        if (widthSizeClass == WindowWidthSizeClass.COMPACT || widthSizeClass == WindowWidthSizeClass.MEDIUM) {
            TopAppBarDefaults.pinnedScrollBehavior()
        } else {
            TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        },
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable (ColumnScope.() -> Unit),
) {
    Scaffold(
        topBar = {
            if (widthSizeClass == WindowWidthSizeClass.COMPACT || widthSizeClass == WindowWidthSizeClass.MEDIUM) {
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
                    windowInsets = contentPadding.asWindowInsets()
                        .only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
                    scrollBehavior = scrollBehavior
                )
            } else {
                LargeTopAppBar(
                    title = title,
                    actions = actions,
                    windowInsets = contentPadding.asWindowInsets()
                        .only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
                    scrollBehavior = scrollBehavior
                )
            }
        },
        snackbarHost = snackbarHost,
        contentWindowInsets = contentPadding.asWindowInsets(),
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .drawVerticalScrollbar(scrollState)
                .verticalScroll(scrollState)
                .padding(innerPadding),
            content = content
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsExtraPane(
    title: @Composable () -> Unit,
    onBackClick: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    actions: @Composable (RowScope.() -> Unit) = {},
    widthSizeClass: WindowWidthSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass,
    scrollBehavior: TopAppBarScrollBehavior =
        if (widthSizeClass == WindowWidthSizeClass.COMPACT || widthSizeClass == WindowWidthSizeClass.MEDIUM) {
            TopAppBarDefaults.pinnedScrollBehavior()
        } else {
            TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        },
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable (ColumnScope.() -> Unit),
) {
    Scaffold(
        topBar = {
            if (widthSizeClass == WindowWidthSizeClass.COMPACT || widthSizeClass == WindowWidthSizeClass.MEDIUM) {
                TopAppBar(
                    title = title,
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(imageVector = ComicIcons.ArrowBack, contentDescription = null)
                        }
                    },
                    actions = actions,
                    windowInsets = contentPadding.asWindowInsets()
                        .only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
                    scrollBehavior = scrollBehavior
                )
            } else {
                LargeTopAppBar(
                    title = title,
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(imageVector = ComicIcons.ArrowBack, contentDescription = null)
                        }
                    },
                    actions = actions,
                    windowInsets = contentPadding.asWindowInsets()
                        .only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
                    scrollBehavior = scrollBehavior
                )
            }
        },
        contentWindowInsets = contentPadding.asWindowInsets(),
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .drawVerticalScrollbar(scrollState)
                .verticalScroll(scrollState)
                .padding(innerPadding),
            content = content
        )
    }
}
