package com.sorrowblue.comicviewer.app

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.animateColorAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalContainerColor
import com.sorrowblue.comicviewer.framework.ui.LocalAppState
import com.sorrowblue.comicviewer.framework.ui.animation.Transitions
import com.sorrowblue.comicviewer.framework.ui.canonical.isNavigationRail

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
