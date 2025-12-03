package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.LocalSharedTransitionScope
import com.sorrowblue.comicviewer.framework.ui.canonical.isNavigationBar

@Composable
fun AdaptiveAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable (RowScope.() -> Unit) = {},
    bottomComponent: @Composable ColumnScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val navigationSuiteType =
        NavigationSuiteScaffoldDefaults.navigationSuiteType(currentWindowAdaptiveInfo())
    val colors = if (navigationSuiteType.isNavigationBar) {
        TopAppBarDefaults.topAppBarColors()
    } else {
        TopAppBarDefaults.topAppBarColors(
            containerColor = ComicTheme.colorScheme.surfaceContainer,
            scrolledContainerColor = ComicTheme.colorScheme.surfaceContainer,
        )
    }
    val targetColor by remember(colors, scrollBehavior) {
        derivedStateOf {
            val overlappingFraction = scrollBehavior?.state?.overlappedFraction ?: 0f
            colors.containerColor(if (overlappingFraction > 0.01f) 1f else 0f)
        }
    }

    val appBarContainerColor = animateColorAsState(
        targetColor,
        animationSpec = spring(dampingRatio = 1.0f, stiffness = 1600.0f),
        label = "appBarContainerColor",
    )
    Column(
        modifier = modifier.drawBehind {
            val color = appBarContainerColor.value
            if (color != Color.Unspecified) {
                drawRect(color = color)
            }
        },
    ) {
        with(LocalSharedTransitionScope.current) {
            with(LocalNavAnimatedContentScope.current) {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier
                                .animateEnterExit(
                                    enter = AppBarTitleTransitionEnter,
                                    exit = AppBarTitleTransitionExit,
                                ).sharedElement(
                                    animatedVisibilityScope = this,
                                    sharedContentState = rememberSharedContentState(
                                        TopAppBarTitleSharedElementKey,
                                    ),
                                ),
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
                    windowInsets = WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal + WindowInsetsSides.Top,
                    ),
                    modifier = Modifier
                        .sharedElement(
                            animatedVisibilityScope = this,
                            sharedContentState = rememberSharedContentState(
                                TopAppBarSharedElementKey,
                            ),
                        ),
                )
            }
        }
        bottomComponent()
    }
}

data object TopAppBarSharedElementKey

data object TopAppBarTitleSharedElementKey

@Stable
private fun TopAppBarColors.containerColor(colorTransitionFraction: Float): Color = lerp(
    containerColor,
    scrolledContainerColor,
    FastOutLinearInEasing.transform(colorTransitionFraction),
)

internal val AppBarTitleTransitionEnter
    @Composable
    get() = fadeIn(
        animationSpec = ComicTheme.motionScheme.slowSpatialSpec(),
        initialAlpha = 0.0f,
    )

internal val AppBarTitleTransitionExit
    @Composable
    get() = fadeOut(
        animationSpec = ComicTheme.motionScheme.defaultSpatialSpec(),
        targetAlpha = 0.0f,
    )
