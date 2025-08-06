package com.sorrowblue.comicviewer.framework.ui.canonical

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.framework.designsystem.theme.ExpressiveMotion
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffoldState

@OptIn(ExperimentalSharedTransitionApi::class)
val CanonicalScaffoldState<*>.DefaultNavigationRail: @Composable (Boolean, @Composable () -> Unit) -> Unit
    get() = { visible, content ->
        AnimatedContent(
            targetState = visible,
            transitionSpec = NavigationRailTransition,
            modifier = Modifier
                .animateEnterExit(
                    enter = NavigationRailTransitionEnter,
                    exit = NavigationRailTransitionExit
                )
                .sharedElement(
                    animatedVisibilityScope = this,
                    sharedContentState = rememberSharedContentState(
                        NavigationRailSharedElementKey
                    )
                ),
        ) {
            if (it) {
                content()
            } else {
                Spacer(Modifier.fillMaxHeight())
            }
        }
    }

internal val NavigationRailTransitionEnter = slideInHorizontally(
    animationSpec = ExpressiveMotion.Spatial.default(),
    initialOffsetX = { -it }
)

internal val NavigationRailTransitionExit = slideOutHorizontally(
    animationSpec = ExpressiveMotion.Spatial.default(),
    targetOffsetX = { -it }
)

internal val NavigationRailTransition: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
    NavigationRailTransitionEnter togetherWith NavigationRailTransitionExit
}

internal data object NavigationRailSharedElementKey
