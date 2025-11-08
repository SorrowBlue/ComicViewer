package com.sorrowblue.comicviewer.app

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.sorrowblue.comicviewer.framework.ui.animation.Transitions

/** ComicViewerApp */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun ComicViewerApp() {
    Transitions.InitSlideDistance()
    SharedTransitionLayout {
//        val state = rememberComicViewerAppState(this)
//        val containerColor by animateColorAsState(
//            if (state.navigationSuiteType.isNavigationRail) ComicTheme.colorScheme.surfaceContainer else ComicTheme.colorScheme.surface
//        )
//        CompositionLocalProvider(
//            LocalAppState provides state,
//            LocalContainerColor provides containerColor
//        ) {
//        }
    }
}
