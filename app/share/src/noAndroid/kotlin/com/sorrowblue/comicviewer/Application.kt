/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.sorrowblue.comicviewer.app.AppGraph
import com.sorrowblue.comicviewer.app.ComicViewerUI
import com.sorrowblue.comicviewer.app.rememberComicViewerUIState
import com.sorrowblue.comicviewer.framework.common.Initializer
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.require

@Composable
context(context: PlatformContext, appGraph: AppGraph)
fun Application(finishApp: () -> Unit) {
    val state = rememberComicViewerUIState()
    ComicViewerUI(finishApp = finishApp, state = state)
    LaunchedEffect(Unit) {
        Initializer.initialize(
            context.require<InitializerContext.Factory>()
                .createInitializerContext().initializer.toList(),
        )
    }
}
