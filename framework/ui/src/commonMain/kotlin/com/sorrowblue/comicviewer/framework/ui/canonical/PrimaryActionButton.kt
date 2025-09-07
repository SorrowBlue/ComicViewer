package com.sorrowblue.comicviewer.framework.ui.canonical

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonMenuScope
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallExtendedFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ExpressiveMotion
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffoldState

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CanonicalScaffoldState<*>.PrimaryActionButton(
    onClick: () -> Unit,
    text: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
) {
    if (navigationSuiteType.isNavigationRail) {
        SmallExtendedFloatingActionButton(
            expanded = navigationRailState.targetValue == WideNavigationRailValue.Expanded,
            onClick = onClick,
            text = text,
            icon = icon,
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
            modifier = modifier.padding(start = 20.dp)
                .animateFloatingActionButton(
                    visible = floatingActionButtonState.targetValue.isVisible,
                    alignment = Alignment.Center
                )
                .animateEnterExit(
                    enter = FloatingActionButtonTransitionEnter,
                    exit = FloatingActionButtonTransitionExit
                )
                .sharedElement(
                    sharedContentState = rememberSharedContentState("fab"),
                    animatedVisibilityScope = this
                )
        )
    } else {
        FloatingActionButton(
            onClick = onClick,
            modifier = modifier
                .animateFloatingActionButton(
                    visible = visible && floatingActionButtonState.targetValue.isVisible,
                    alignment = Alignment.BottomEnd
                )
                .animateEnterExit(
                    enter = FloatingActionButtonTransitionEnter,
                    exit = FloatingActionButtonTransitionExit
                )
                .sharedElement(
                    sharedContentState = rememberSharedContentState("fab"),
                    animatedVisibilityScope = this
                )
        ) {
            Icon(imageVector = ComicIcons.Add, contentDescription = null)
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CanonicalScaffoldState<*>.PrimaryActionButtonMenu(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    content: @Composable FloatingActionButtonMenuScope.() -> Unit,
) {
    var fabMenuExpanded by remember { mutableStateOf(false) }
    androidx.compose.material3.FloatingActionButtonMenu(
        expanded = fabMenuExpanded,
        button = {
            ToggleFloatingActionButton(
                modifier =
                Modifier.semantics {
                    traversalIndex = -1f
                    stateDescription = if (fabMenuExpanded) "Expanded" else "Collapsed"
                    contentDescription = "Toggle menu"
                }
                    .animateFloatingActionButton(
                        visible = visible && floatingActionButtonState.targetValue.isVisible || fabMenuExpanded,
                        alignment = Alignment.BottomEnd
                    )
                    .animateEnterExit(
                        enter = FloatingActionButtonTransitionEnter,
                        exit = FloatingActionButtonTransitionExit
                    )
                    .sharedElement(
                        sharedContentState = rememberSharedContentState("fab"),
                        animatedVisibilityScope = this
                    ),
                checked = fabMenuExpanded,
                onCheckedChange = { fabMenuExpanded = !fabMenuExpanded }
            ) {
                val imageVector by remember(checkedProgress) {
                    derivedStateOf {
                        if (checkedProgress > 0.5f) ComicIcons.Close else ComicIcons.Add
                    }
                }
                Icon(
                    imageVector,
                    contentDescription = null,
                    modifier = Modifier.animateIcon({ checkedProgress })
                )
            }
        },
        content = content,
        modifier = modifier
    )
}

private val FloatingActionButtonTransitionEnter = scaleIn(
    animationSpec = ExpressiveMotion.Spatial.fast(),
    initialScale = 0f
) + fadeIn(
    animationSpec = ExpressiveMotion.Effects.fast(),
    initialAlpha = 0f
)

private val FloatingActionButtonTransitionExit = scaleOut(
    animationSpec = ExpressiveMotion.Spatial.fast(),
    targetScale = 0f
) + fadeOut(
    animationSpec = ExpressiveMotion.Effects.fast(),
    targetAlpha = 0f
)
