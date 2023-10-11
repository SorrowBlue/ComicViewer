package com.sorrowblue.comicviewer.framework.ui.responsive

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.copy

@Stable
data class AppScaffoldState internal constructor(
    val isVisibleFab: Boolean,
)

@Composable
fun rememberAppScaffold(
    isVisibleFab: Boolean = false,
): AppScaffoldState {
    return remember(isVisibleFab) {
        AppScaffoldState(
            isVisibleFab = isVisibleFab,
        )
    }
}

@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    state: AppScaffoldState = rememberAppScaffold(),
    topBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    windowSizeClass: WindowSizeClass = LocalWindowSize.current,
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable (PaddingValues) -> Unit,
) {
    val isCompact = remember(windowSizeClass.widthSizeClass) {
        windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    }
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        snackbarHost = snackbarHost,
        containerColor = if (isCompact) ComicTheme.colorScheme.surface else ComicTheme.colorScheme.surfaceContainer,
        contentWindowInsets = contentWindowInsets
    ) { innerPadding ->
        val end =
            innerPadding.calculateEndPadding(LocalLayoutDirection.current) + ComicTheme.dimension.margin
        val padding = if (isCompact) {
            innerPadding
                .add(
                    PaddingValues(
                        bottom = ComicTheme.dimension.margin + if (state.isVisibleFab) 56.dp + 16.dp else 0.dp
                    )
                )
        } else {
            innerPadding
                .copy(end = end)
                .add(
                    paddingValues = PaddingValues(
                        top = ComicTheme.dimension.spacer,
                        bottom = ComicTheme.dimension.margin
                    )
                )
        }
        content(padding)
    }
}
