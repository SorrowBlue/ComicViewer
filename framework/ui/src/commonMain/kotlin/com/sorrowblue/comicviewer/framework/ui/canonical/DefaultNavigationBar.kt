package com.sorrowblue.comicviewer.framework.ui.canonical

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.framework.designsystem.theme.ExpressiveMotion
import com.sorrowblue.comicviewer.framework.ui.NavigationSuiteScaffold2State

@OptIn(ExperimentalSharedTransitionApi::class)
val NavigationSuiteScaffold2State<*>.DefaultNavigationBar: @Composable (Boolean, @Composable () -> Unit) -> Unit
    get() = { visible, content ->
        AnimatedContent(
            targetState = visible,
            transitionSpec = NavigationBarTransition,
            modifier = Modifier
                .animateEnterExit(
                    enter = NavigationBarTransitionEnter,
                    exit = NavigationBarTransitionExit
                )
                .sharedElement(
                    animatedVisibilityScope = this,
                    sharedContentState = rememberSharedContentState(
                        NavigationBarSharedElementKey
                    )
                )
        ) {
            if (visible) {
                content()
            } else {
                Spacer(Modifier.fillMaxWidth())
            }
        }
    }

internal val NavigationBarTransitionEnter = slideInVertically(
    animationSpec = ExpressiveMotion.Spatial.default(),
    initialOffsetY = { it }
)

internal val NavigationBarTransitionExit = slideOutVertically(
    animationSpec = ExpressiveMotion.Spatial.default(),
    targetOffsetY = { it }
)

internal val NavigationBarTransition: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
    NavigationBarTransitionEnter togetherWith NavigationBarTransitionExit
}

internal data object NavigationBarSharedElementKey
