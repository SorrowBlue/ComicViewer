/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.foundation.layout.offset
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuScope
import androidx.compose.material3.Icon
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons.Add
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons.Close

/**
 * Scope for [PrimaryActionButtonMenu] content providing [floatingActionButtonState].
 */
interface PrimaryActionButtonMenuScope : FloatingActionButtonMenuScope {
    val floatingActionButtonState: FloatingActionButtonState
}

private class PrimaryActionButtonMenuScopeImpl(
    private val scaffoldState: AdaptiveNavigationSuiteScaffoldState,
    menuScope: FloatingActionButtonMenuScope,
) : PrimaryActionButtonMenuScope,
    FloatingActionButtonMenuScope by menuScope {
    override val floatingActionButtonState: FloatingActionButtonState
        get() = scaffoldState.floatingActionButtonState
}

/**
 * Convenience wrapper for [PrimaryActionButtonMenu] using [LocalAdaptiveNavigationSuiteScaffoldState].
 */
@Composable
fun PrimaryActionButtonMenu(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    content: @Composable PrimaryActionButtonMenuScope.() -> Unit,
) {
    val scaffoldState = LocalAdaptiveNavigationSuiteScaffoldState.current
    if (scaffoldState != null) {
        with(scaffoldState) {
            PrimaryActionButtonMenu(
                modifier = modifier,
                visible = visible,
            ) {
                val menuScope = this
                val scope = remember(menuScope, scaffoldState) {
                    PrimaryActionButtonMenuScopeImpl(scaffoldState, menuScope)
                }
                with(scope) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun AdaptiveNavigationSuiteScaffoldState.PrimaryActionButtonMenu(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    content: @Composable FloatingActionButtonMenuScope.() -> Unit,
) {
    FloatingActionButtonMenu(
        expanded = floatingActionButtonState.menuExpanded,
        button = {
            with(LocalNavAnimatedContentScope.current) {
                ToggleFloatingActionButton(
                    modifier =
                        Modifier
                            .semantics {
                                traversalIndex = -1f
                                stateDescription =
                                    if (floatingActionButtonState.menuExpanded) "Expanded" else "Collapsed"
                                contentDescription = "Toggle menu"
                            }.animateFloatingActionButton(
                                visible =
                                    visible &&
                                        (
                                            floatingActionButtonState.targetValue.isVisible ||
                                                floatingActionButtonState.menuExpanded
                                            ),
                                alignment = Alignment.BottomEnd,
                            ).animateEnterExit(
                                enter = FloatingActionButtonTransitionEnter,
                                exit = FloatingActionButtonTransitionExit,
                            ),
                    checked = floatingActionButtonState.menuExpanded,
                    containerSize = if (navigationSuiteType.isNavigationRail) {
                        ToggleFloatingActionButtonDefaults
                            .containerSizeMedium()
                    } else {
                        ToggleFloatingActionButtonDefaults
                            .containerSize()
                    },
                    onCheckedChange = floatingActionButtonState::toggleMenu,
                ) {
                    val imageVector by remember(checkedProgress) {
                        derivedStateOf {
                            if (checkedProgress > 0.5f) Close else Add
                        }
                    }
                    Icon(
                        imageVector,
                        contentDescription = null,
                        modifier = Modifier.animateIcon(
                            checkedProgress = { checkedProgress },
                            size = if (navigationSuiteType.isNavigationRail) {
                                ToggleFloatingActionButtonDefaults
                                    .iconSizeMedium()
                            } else {
                                ToggleFloatingActionButtonDefaults.iconSize()
                            },
                        ),
                    )
                }
            }
        },
        content = content,
        modifier = modifier.offset(16.dp, 16.dp), // FloatingActionButtonMenuの余分なPadding対策
    )
}
