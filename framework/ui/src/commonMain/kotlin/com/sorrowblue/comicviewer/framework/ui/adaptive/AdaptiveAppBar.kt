/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.animation.LocalSharedTransitionScope

@Composable
fun AdaptiveAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable (RowScope.() -> Unit) = {},
    windowInsets: WindowInsets = WindowInsets.safeDrawing.only(
        WindowInsetsSides.Horizontal + WindowInsetsSides.Top,
    ),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    sharedAnimation: Boolean = true,
) {
    val navigationSuiteType = currentNavigationSuiteType()
    val colors = if (navigationSuiteType.isNavigationBar) {
        TopAppBarDefaults.topAppBarColors()
    } else {
        TopAppBarDefaults.topAppBarColors(
            containerColor = ComicTheme.colorScheme.surfaceContainer,
            scrolledContainerColor = ComicTheme.colorScheme.surfaceContainer,
        )
    }
    with(LocalSharedTransitionScope.current) {
        with(LocalNavAnimatedContentScope.current) {
            TopAppBar(
                title = {
                    Box(
                        modifier = if (sharedAnimation) {
                            Modifier
                                .animateEnterExit(
                                    enter = AppBarTitleTransitionEnter,
                                    exit = AppBarTitleTransitionExit,
                                ).sharedElement(
                                    animatedVisibilityScope = this,
                                    sharedContentState = rememberSharedContentState(
                                        TopAppBarTitleSharedElementKey,
                                    ),
                                )
                        } else {
                            Modifier
                        },
                    ) {
                        title()
                    }
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .animateEnterExit(
                                enter = AppBarTitleTransitionEnter,
                                exit = AppBarTitleTransitionExit,
                            ),
                    ) {
                        navigationIcon()
                    }
                },
                actions = actions,
                colors = colors,
                scrollBehavior = scrollBehavior,
                windowInsets = windowInsets,
                modifier = modifier.then(
                    if (sharedAnimation) {
                        Modifier.sharedElement(
                            animatedVisibilityScope = this,
                            sharedContentState = rememberSharedContentState(
                                TopAppBarSharedElementKey,
                            ),
                        )
                    } else {
                        Modifier
                    },
                ),
            )
        }
    }
}

private data object TopAppBarSharedElementKey

private data object TopAppBarTitleSharedElementKey

private val AppBarTitleTransitionEnter
    @ReadOnlyComposable
    @Composable
    get() = fadeIn(
        animationSpec = ComicTheme.motionScheme.slowSpatialSpec(),
        initialAlpha = 0.0f,
    )

private val AppBarTitleTransitionExit
    @ReadOnlyComposable
    @Composable
    get() = fadeOut(
        animationSpec = ComicTheme.motionScheme.defaultSpatialSpec(),
        targetAlpha = 0.0f,
    )
