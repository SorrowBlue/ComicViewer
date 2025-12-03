package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuScope
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallExtendedFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.WideNavigationRailValue
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
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.canonical.isNavigationRail
import com.sorrowblue.comicviewer.framework.ui.canonical.isVisible

@Composable
fun AdaptiveNavigationSuiteScaffoldState.PrimaryActionButton(
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

@Composable
fun AdaptiveNavigationSuiteScaffoldState.PrimaryActionButtonMenu(
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
                            visible && floatingActionButtonState.targetValue.isVisible ||
                                floatingActionButtonState.menuExpanded,
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
                            if (checkedProgress > 0.5f) ComicIcons.Close else ComicIcons.Add
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

private val FloatingActionButtonTransitionEnter
    @Composable
    get() = scaleIn(
        animationSpec = ComicTheme.motionScheme.fastSpatialSpec(),
        initialScale = 0f,
    ) + fadeIn(
        animationSpec = ComicTheme.motionScheme.fastEffectsSpec(),
        initialAlpha = 0f,
    )

private val FloatingActionButtonTransitionExit
    @Composable
    get() = scaleOut(
        animationSpec = ComicTheme.motionScheme.fastSpatialSpec(),
        targetScale = 0f,
    ) + fadeOut(
        animationSpec = ComicTheme.motionScheme.fastEffectsSpec(),
        targetAlpha = 0f,
    )
