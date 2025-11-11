package com.sorrowblue.comicviewer

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sorrowblue.comicviewer.app.MainViewModel
import com.sorrowblue.comicviewer.framework.common.Initializer
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.require
import com.sorrowblue.comicviewer.framework.ui.animation.materialSharedAxisZIn
import com.sorrowblue.comicviewer.framework.ui.animation.materialSharedAxisZOut
import io.github.takahirom.rin.collectAsRetainedState

@Composable
context(context: PlatformContext)
fun Application(finishApp: () -> Unit) {
    val enter = materialSharedAxisZIn()
    val exit = materialSharedAxisZOut()
    val viewmodel = viewModel { MainViewModel() }
    val shouldKeepSplash by viewmodel.shouldKeepSplash.collectAsRetainedState()
    val uiContext = rememberComicViewerUIContext()
    with(uiContext) {
        val comicViewerUIState = rememberComicViewerUIState()
        ComicViewerUI(
            state = comicViewerUIState,
            finishApp = finishApp,
        )
        AnimatedContent(
            shouldKeepSplash,
            transitionSpec = { enter togetherWith exit },
        ) { shouldKeepSplash ->
            if (shouldKeepSplash) {
                SplashScreen()
            }
        }
    }
    LaunchedEffect(Unit) {
        Initializer.initialize(context.require<AppContext>().initializer.toList())
    }
}
