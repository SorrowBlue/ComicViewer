/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.FloatingActionButtonValue
import com.sorrowblue.comicviewer.framework.ui.adaptive.currentNavigationSuiteType
import com.sorrowblue.comicviewer.framework.ui.adaptive.isNavigationBar
import com.sorrowblue.comicviewer.framework.ui.adaptive.isNavigationRail
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberFloatingActionButtonState

@Composable
internal fun FloatingActionContainer(
    state: AppFloatingActionBarState,
    isRail: Boolean,
    navigationSuiteType: NavigationSuiteType = currentNavigationSuiteType(),
) {
    if (isRail) {
        RailFabContainer(
            state = state,
            navigationSuiteType = navigationSuiteType,
        )
    } else {
        BottomFabContainer(
            state = state,
            navigationSuiteType = navigationSuiteType,
        )
    }
}

@Composable
private fun BottomFabContainer(
    state: AppFloatingActionBarState,
    navigationSuiteType: NavigationSuiteType = currentNavigationSuiteType(),
) {
    state.floatingActionContents.forEach {
        val floatingActionButtonState =
            state.fabStateList.getOrPut(it.navKey) { rememberFloatingActionButtonState() }
        val isCurrent = state.isCurrent(it.navKey)
        val visible = isCurrent &&
            (navigationSuiteType.isNavigationBar || !it.allowRail)
        LaunchedEffect(visible) {
            floatingActionButtonState.snapTo(
                if (visible) FloatingActionButtonValue.Visible else FloatingActionButtonValue.Hidden,
            )
        }
        SideEffect(isCurrent) {
            if (!isCurrent) {
                floatingActionButtonState.toggleMenu(false)
            }
        }
        it.Content(
            state = floatingActionButtonState,
            modifier = Modifier.animateFloatingActionButton(
                visible = visible,
                alignment = Alignment.BottomEnd,
            ),
            elevation = FloatingActionButtonDefaults.elevation(),
            expanded = floatingActionButtonState.expand,
        )
    }
}

@Composable
private fun RailFabContainer(
    state: AppFloatingActionBarState,
    navigationSuiteType: NavigationSuiteType = currentNavigationSuiteType(),
) {
    state.floatingActionContents.forEach {
        if (it.allowRail) {
            val floatingActionButtonState =
                state.fabStateList.getOrPut(it.navKey) { rememberFloatingActionButtonState() }
            val isCurrent = state.isCurrent(it.navKey)
            val visible = isCurrent && navigationSuiteType.isNavigationRail
            SideEffect(isCurrent) {
                if (!isCurrent) {
                    floatingActionButtonState.toggleMenu(false)
                }
            }
            AnimatedVisibility(
                visible = visible,
                enter = FloatingActionButtonTransitionEnter,
                exit = FloatingActionButtonTransitionExit,
            ) {
                it.Content(
                    state = floatingActionButtonState,
                    modifier = Modifier,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                    expanded =
                        navigationSuiteType ==
                            NavigationSuiteType.WideNavigationRailExpanded,
                )
            }
        }
    }
}

private val FloatingActionButtonTransitionEnter
    @ReadOnlyComposable
    @Composable
    get() = scaleIn(
        animationSpec = ComicTheme.motionScheme.fastSpatialSpec(),
        initialScale = 0f,
    ) + fadeIn(
        animationSpec = ComicTheme.motionScheme.fastEffectsSpec(),
        initialAlpha = 0f,
    ) + expandVertically(
        expandFrom = Alignment.CenterVertically,
        animationSpec = ComicTheme.motionScheme.fastEffectsSpec(),
        clip = false,
    )

private val FloatingActionButtonTransitionExit
    @ReadOnlyComposable
    @Composable
    get() = scaleOut(
        animationSpec = ComicTheme.motionScheme.fastSpatialSpec(),
        targetScale = 0f,
    ) + fadeOut(
        animationSpec = ComicTheme.motionScheme.fastEffectsSpec(),
        targetAlpha = 0f,
    ) + shrinkVertically(
        shrinkTowards = Alignment.CenterVertically,
        animationSpec = ComicTheme.motionScheme.fastEffectsSpec(),
        clip = false,
    )
