package com.sorrowblue.comicviewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.sorrowblue.comicviewer.framework.common.Initializer
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.require

@Composable
context(context: PlatformContext)
fun Application(finishApp: () -> Unit) {
    with(rememberComicViewerUIContext()) {
        val state = rememberComicViewerUIState()
        ComicViewerUI(finishApp = finishApp, state = state)
    }
    LaunchedEffect(Unit) {
        Initializer.initialize(context.require<AppContext>().initializer.toList())
    }
}
