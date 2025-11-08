package com.sorrowblue.comicviewer.framework.ui.canonical

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

internal val NavigationRailTransitionEnter
    @Composable
    get() = slideInHorizontally(
        animationSpec = ComicTheme.motionScheme.defaultSpatialSpec(),
        initialOffsetX = { -it },
    )

internal val NavigationRailTransitionExit
    @Composable
    get() = slideOutHorizontally(
        animationSpec = ComicTheme.motionScheme.defaultSpatialSpec(),
        targetOffsetX = { -it },
    )

internal data object NavigationRailSharedElementKey
