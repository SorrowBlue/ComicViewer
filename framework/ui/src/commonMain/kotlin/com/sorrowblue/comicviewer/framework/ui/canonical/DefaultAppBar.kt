package com.sorrowblue.comicviewer.framework.ui.canonical

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.framework.designsystem.theme.ExpressiveMotion
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffoldState

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CanonicalScaffoldState<*>.DefaultAppBarScope(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    AnimatedContent(
        appBarState.targetValue.isVisible,
        transitionSpec = AppBarTransition,
        modifier = modifier
            .animateEnterExit(
                enter = AppBarTransitionEnter,
                exit = AppBarTransitionExit
            )
            .sharedElement(
                animatedVisibilityScope = this,
                sharedContentState = rememberSharedContentState(AppBarSharedElementKey),
            ),
    ) {
        if (it) {
            content()
        } else {
            Spacer(
                Modifier.fillMaxWidth()
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
                    )
            )
        }
    }
}

private val AppBarTransitionEnter = slideInVertically(
    animationSpec = ExpressiveMotion.Spatial.default(),
    initialOffsetY = { -it }
)
private val AppBarTransitionExit = slideOutVertically(
    animationSpec = ExpressiveMotion.Spatial.default(),
    targetOffsetY = { -it }
)
private val AppBarTransition: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
    AppBarTransitionEnter togetherWith AppBarTransitionExit
}

private data object AppBarSharedElementKey
