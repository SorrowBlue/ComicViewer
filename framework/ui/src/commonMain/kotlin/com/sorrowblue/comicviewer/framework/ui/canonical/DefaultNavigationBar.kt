package com.sorrowblue.comicviewer.framework.ui.canonical

import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

internal val NavigationBarTransitionEnter
    @Composable
    get() = slideInVertically(
        animationSpec = ComicTheme.motionScheme.defaultSpatialSpec(),
        initialOffsetY = { it },
    )

internal val NavigationBarTransitionExit
    @Composable
    get() = slideOutVertically(
        animationSpec = ComicTheme.motionScheme.defaultSpatialSpec(),
        targetOffsetY = { it },
    )

internal data object NavigationBarSharedElementKey
