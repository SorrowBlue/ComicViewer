/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallExtendedFloatingActionButton
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

/**
 * Convenience wrapper for [PrimaryActionButton] using [LocalAdaptiveNavigationSuiteScaffoldState].
 */
@Composable
fun PrimaryActionButton(
    onClick: () -> Unit,
    text: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
) {
    PrimaryActionContent {
        val scaffoldState = LocalAdaptiveNavigationSuiteScaffoldState.current
        scaffoldState?.PrimaryActionButton(
            onClick = onClick,
            text = text,
            icon = icon,
            modifier = modifier,
            visible = visible,
        )
    }
}

/**
 * Provides content to the primary action slot (e.g. Floating Action Button) of the enclosing
 * [AdaptiveNavigationSuiteScaffold].
 */
@Composable
private fun PrimaryActionContent(content: @Composable () -> Unit) {
    val slot = LocalPrimaryActionSlot.current ?: return
    DisposableEffect(content) {
        slot.value = content
        onDispose {
            slot.value = null
        }
    }
}

@Composable
private fun AdaptiveNavigationSuiteScaffoldState.PrimaryActionButton(
    onClick: () -> Unit,
    text: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
) {
    with(LocalNavAnimatedContentScope.current) {
        if (navigationSuiteType.isNavigationRail) {
            SmallExtendedFloatingActionButton(
                expanded = wideNavigationRailState.targetValue == WideNavigationRailValue.Expanded,
                onClick = onClick,
                text = text,
                icon = icon,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                modifier = modifier
                    .padding(start = 20.dp)
                    .animateFloatingActionButton(
                        visible = floatingActionButtonState.targetValue.isVisible,
                        alignment = Alignment.Center,
                    ).animateEnterExit(
                        enter = FloatingActionButtonTransitionEnter,
                        exit = FloatingActionButtonTransitionExit,
                    ),
            )
        } else {
            FloatingActionButton(
                onClick = onClick,
                modifier = modifier
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.End))
                    .animateFloatingActionButton(
                        visible = visible && floatingActionButtonState.targetValue.isVisible,
                        alignment = Alignment.BottomEnd,
                    ).animateEnterExit(
                        enter = FloatingActionButtonTransitionEnter,
                        exit = FloatingActionButtonTransitionExit,
                    ),
            ) {
                Icon(imageVector = ComicIcons.Add, contentDescription = null)
            }
        }
    }
}

internal val FloatingActionButtonTransitionEnter
    @ReadOnlyComposable
    @Composable
    get() = scaleIn(
        animationSpec = ComicTheme.motionScheme.fastSpatialSpec(),
        initialScale = 0f,
    ) + fadeIn(
        animationSpec = ComicTheme.motionScheme.fastEffectsSpec(),
        initialAlpha = 0f,
    )

internal val FloatingActionButtonTransitionExit
    @ReadOnlyComposable
    @Composable
    get() = scaleOut(
        animationSpec = ComicTheme.motionScheme.fastSpatialSpec(),
        targetScale = 0f,
    ) + fadeOut(
        animationSpec = ComicTheme.motionScheme.fastEffectsSpec(),
        targetAlpha = 0f,
    )
